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

import com.netflix.spinnaker.kork.dynamicconfig.DynamicConfigService;
import java.util.List;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class RequestResponseSizeScoreJudge implements ScoreJudge {

  private static final Logger log = LoggerFactory.getLogger(RequestResponseSizeScoreJudge.class);

  private static final double DEFAULT_BASELINE_BYTES = 1024 * 1024; // 1MB

  private final DynamicConfigService dynamicConfigService;

  public RequestResponseSizeScoreJudge(DynamicConfigService dynamicConfigService) {
    this.dynamicConfigService = dynamicConfigService;
  }

  @Override
  public int score(@NotNull HttpServletRequest request, HttpServletResponse response) {
    if (!(response instanceof ContentCachingResponseWrapper)) {
      return score(request.getContentLength(), 0);
    }

    int requestSize = request.getContentLength();
    int responseSize = ((ContentCachingResponseWrapper) response).getContentSize();

    int score = score(requestSize, responseSize);
    log.trace(
        "Judged request score of {} (request={}, response={})", score, requestSize, responseSize);
    return score;
  }

  @Override
  public int historicalScore(@Nonnull List<Integer> history) {
    return (int)
        Math.max(Math.floor(history.stream().reduce(0, Integer::sum) / (double) history.size()), 1);
  }

  private int score(int requestLength, int responseLength) {
    return (int)
        Math.max(
            Math.floor(
                (requestLength + responseLength)
                    / dynamicConfigService.getConfig(
                        Double.class,
                        "rate-limit.score-judge.baseline-size-bytes",
                        DEFAULT_BASELINE_BYTES)),
            1);
  }
}
