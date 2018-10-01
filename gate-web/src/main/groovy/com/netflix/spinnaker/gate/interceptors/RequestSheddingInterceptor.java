/*
 * Copyright 2018 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.spinnaker.gate.interceptors;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.netflix.spectator.api.Id;
import com.netflix.spectator.api.Registry;
import com.netflix.spinnaker.kork.dynamicconfig.DynamicConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

/**
 * A request interceptor for shedding low-priority requests from Deck.
 *
 * Low priority requests are identified by the X-Spinnaker-Priority
 * request header, which must also include a "low" value. If missing
 * or given any other value, the request will be treated as as normal.
 *
 * Uses the dynamic config service to enable/disable the functionality,
 * and further to optionally drop low priority requests on specific
 * request paths. A path pattern must be defined before any low priority
 * requests will be dropped.
 *
 *  A percentage chance config key is also available which takes a
 * value between 0 and 100, representing the percentage chance that
 * the request will be dropped. If the value is set to "60", there
 * will be a 60% chance the request will be dropped; this value is
 * only considered when the enabled key is also set to "true".
 *
 * If enabled but an internal error occurs due to configuration, the
 * interceptor will elect to drop all low priority requests.
 */
public class RequestSheddingInterceptor extends HandlerInterceptorAdapter {

  final static String PRIORITY_HEADER = "X-Spinnaker-Priority";
  final static String LOW_PRIORITY = "low";

  final static String ENABLED_KEY = "requestShedding";
  final static String CHANCE_KEY = "requestShedding.chance";
  final static String PATHS_KEY = "requestShedding.paths";

  private final static Logger log = LoggerFactory.getLogger(RequestSheddingInterceptor.class);
  private final static Splitter splitter = Splitter.on(",").omitEmptyStrings().trimResults();
  private final static Random random = new Random();

  private final DynamicConfigService configService;
  private final Registry registry;
  private final ScheduledExecutorService executorService;

  private final Id requestsId;

  private final CopyOnWriteArrayList<Pattern> pathPatterns = new CopyOnWriteArrayList<>();

  public RequestSheddingInterceptor(DynamicConfigService configService, Registry registry) {
    this.configService = configService;
    this.registry = registry;

    this.requestsId = registry.createId("requestShedding.requests");

    this.executorService = Executors.newScheduledThreadPool(1);
    this.executorService.scheduleWithFixedDelay(this::compilePatterns, 0, 30, TimeUnit.SECONDS);
  }

  @PreDestroy
  protected void shutdown() {
    this.executorService.shutdown();
  }

  @Override
  public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {
    if (configService.isEnabled(ENABLED_KEY, false) && isDroppable(request)) {
      if (shouldDropRequestWithChance()) {
        log.warn("Dropping low priority request: {}", request.getRequestURI());
        registry.counter(requestsId.withTag("action", "dropped")).increment();

        response.setDateHeader("Retry-After", Instant.now().plus(Duration.ofMinutes(1)).toEpochMilli());
        throw new LowPriorityRequestRejected();
      }
      log.debug("Dice roll prevented low priority request shedding: {}", request.getRequestURI());
      registry.counter(requestsId.withTag("action", "allowed")).increment();
    }

    return super.preHandle(request, response, handler);
  }

  private boolean isDroppable(HttpServletRequest request) {
    String uri = request.getRequestURI();

    // We want our error handler to service the errors correctly
    if ("/error".equals(uri)) {
      return false;
    }

    // until `deck` propagates a `X-Spinnaker-Priority` header, treat every request as sheddable
    String priorityHeader = Optional.ofNullable(request.getHeader(PRIORITY_HEADER)).orElse(LOW_PRIORITY);
    return LOW_PRIORITY.equals(priorityHeader) && hasPathMatch(uri);
  }

  private boolean shouldDropRequestWithChance() {
    Integer chance = configService.getConfig(Integer.class, CHANCE_KEY, 100);
    if (chance < 0 || chance > 100) {
      log.warn("Request shedding drop chance is out of bounds: {}, assuming 100", chance);
      chance = 100;
    }
    return random.nextInt(100) <= chance;
  }

  private boolean hasPathMatch(String requestedPath) {
    if (!pathPatterns.isEmpty()) {
      for (Pattern pattern : pathPatterns) {
        if (pattern.matcher(requestedPath).matches()) {
          return true;
        }
      }
    }
    return false;
  }

  @SuppressWarnings("UnstableApiUsage")
  public void compilePatterns() {
    String pathMatchers = configService.getConfig(String.class, PATHS_KEY, "");
    if (Strings.isNullOrEmpty(pathMatchers)) {
      return;
    }

    List<String> paths = splitter.splitToList(pathMatchers);
    if (paths.isEmpty()) {
      return;
    }

    List<Pattern> newPatterns = new ArrayList<>();
    for (String path : paths) {
      try {
        newPatterns.add(Pattern.compile(path));
      } catch (PatternSyntaxException e) {
        log.error("Path pattern invalid, skipping: {}", path, e);
      }
    }

    pathPatterns.addAll(newPatterns);
    pathPatterns.removeIf(p -> !newPatterns.contains(p));
  }

  @ResponseStatus(value = SERVICE_UNAVAILABLE, reason = LowPriorityRequestRejected.REASON)
  static class LowPriorityRequestRejected extends RuntimeException {
    final static String REASON = "Low priority requests are not currently being accepted";
    LowPriorityRequestRejected() {
      super(REASON);
    }
  }
}
