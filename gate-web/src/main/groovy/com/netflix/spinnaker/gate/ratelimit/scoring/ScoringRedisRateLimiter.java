/*
 * Copyright 2020 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.spinnaker.gate.ratelimit.scoring;

import static java.lang.String.format;

import com.netflix.spinnaker.gate.ratelimit.Rate;
import com.netflix.spinnaker.gate.ratelimit.RateLimitPrincipal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;

/**
 * Rate limits requests based on cost of servicing the request, storing principal request usage in a
 * sliding window.
 *
 * <p>Request cost comes in two forms: "Perceived cost" (PC) and "Actual cost" (AC). While serving
 * any request, the decision to accept a request or not will use the PC supplemented by historical
 * ACs of the request handler.
 *
 * <p>Historical costs use only AC values and are stored without taking into account the request
 * handler's parameter values. This is a balance of having data to calculate a PC that is roughly
 * accurate and actual operational cost of maintaining a large amount of historical information.
 *
 * <p>Costs are calculated by a {@link ScoreJudge}. If the ScoreJudge is unable to determine the
 * score for a particular request, a default PC will be calculated using only request information.
 * Should a principal have full capacity, but the request will be allowed regardless of request
 * cost.
 */
public class ScoringRedisRateLimiter implements ScoringRateLimiter {

  private static final Logger log = LoggerFactory.getLogger(ScoringRateLimiter.class);

  private static final int NUM_SCORES = 100;

  private final JedisPool jedisPool;
  private final ScoreJudge scoreJudge;
  private final Clock clock;

  public ScoringRedisRateLimiter(JedisPool jedisPool, ScoreJudge scoreJudge, Clock clock) {
    this.jedisPool = jedisPool;
    this.scoreJudge = scoreJudge;
    this.clock = clock;
  }

  @Override
  public Rate incrementRate(
      @Nonnull RateLimitPrincipal principal,
      @Nonnull HttpServletRequest request,
      @Nullable String handlerMethod) {
    Rate rate = new Rate();

    final String key = principalKey(principal.getName());
    try (Jedis jedis = jedisPool.getResource()) {
      // Calculate the score that will be used for the existing request
      int score = getRequestScore(request, handlerMethod);
      rate.setRequestCost(score);

      // Get the remaining capacity for the principal
      final long now = clock.millis();
      final long window = clock.instant().minus(Duration.ofHours(1)).toEpochMilli();

      // Age-out old requests, range the set of requests for the principal and reset the expiry
      Transaction txn = jedis.multi();
      txn.zremrangeByScore(key, 0, window);
      txn.zrange(key, 0, -1);
      txn.expire(key, (int) window / 1_000);
      List<Object> result = txn.exec();

      // zrange result: History of previously used request in the window
      @SuppressWarnings("unchecked")
      final int usedRequests =
          ((Set<String>) result.get(1)).stream().map(Integer::valueOf).reduce(0, Integer::sum);

      if (usedRequests == 0) {
        // It's cool dog, just allow it regardless of cost or capacity.
        rate.setThrottled(false);
        rate.setRemaining(principal.getCapacity() - score);
      } else if (usedRequests + score > principal.getCapacity()) {
        rate.setThrottled(true);
        rate.setRemaining(principal.getCapacity());
      } else {
        rate.setThrottled(false);
        rate.setRemaining(principal.getCapacity() - (usedRequests + score));
      }

      // Add the request if this request is actually accepted
      if (!rate.isThrottled()) {
        jedis.zadd(key, now, String.valueOf(score));
      }
    }

    rate.setRateSeconds(1);
    rate.setCapacity(principal.getCapacity());
    rate.setReset(Instant.now().plus(Duration.ofHours(1)).toEpochMilli());

    return rate;
  }

  @Override
  public int calculateTotalCost(
      @Nonnull HttpServletRequest request,
      @Nonnull HttpServletResponse response,
      @Nullable String handlerMethod) {
    final int score = scoreJudge.score(request, response);

    if (handlerMethod == null) {
      // If the handlerMethod couldn't be resolved, we can't save this invocation, so just return
      // the score.
      return score;
    }

    // Persist the actual total score of the request, keeping the last 100 scores for the handler.
    final String key = handlerKey(handlerMethod);
    try (Jedis jedis = jedisPool.getResource()) {
      Pipeline p = jedis.pipelined();
      p.rpush(key, String.valueOf(score));
      p.ltrim(key, 0, NUM_SCORES - 1);
      p.expire(key, 3_600);
      p.sync();
    } catch (Exception e) {
      log.error("Failed updating request score ({}) for '{}'", score, key, e);
      return score;
    }

    return score;
  }

  private int getRequestScore(HttpServletRequest request, String handlerMethod) {
    if (handlerMethod == null) {
      // No handlerMethod, so we can only operate off of whatever score is generated from the
      // request itself.
      return scoreJudge.score(request, null);
    }

    final String key = handlerKey(handlerMethod);
    try (Jedis jedis = jedisPool.getResource()) {
      return scoreJudge.historicalScore(
          jedis.lrange(key, 0, NUM_SCORES - 1).stream()
              .map(Integer::valueOf)
              .collect(Collectors.toList()));
    } catch (Exception e) {
      final int requestOnlyScore = scoreJudge.score(request, null);
      log.error(
          "Failed calculating score for '{}', falling back to request score ({})",
          key,
          requestOnlyScore,
          e);
      return requestOnlyScore;
    }
  }

  private static String handlerKey(String handlerMethod) {
    return format("rateLimitHistory:%s", handlerMethod);
  }

  private static String principalKey(String principalName) {
    return format("rateLimitWindow:%s", principalName);
  }
}
