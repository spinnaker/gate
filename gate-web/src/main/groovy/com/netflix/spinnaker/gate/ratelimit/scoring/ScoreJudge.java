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

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Scores a Request / Response for rate limiting. */
public interface ScoreJudge {

  /**
   * Creates a score for the given request & response.
   *
   * <p>It is possible that {@code response} will be null if there is no historical score.
   *
   * @param request
   * @param response
   * @return The total cost of this request. Must be positive.
   */
  int score(@Nonnull HttpServletRequest request, @Nullable HttpServletResponse response);

  /** Returns a score given a list of historical scores. */
  int historicalScore(@Nonnull List<Integer> historicalScores);
}
