/*
 * Copyright 2017 Netflix, Inc.
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
package com.netflix.spinnaker.gate.ratelimit;

import com.netflix.spinnaker.gate.config.RateLimiterConfiguration;

public class StaticRateLimitPrincipalProvider extends AbstractRateLimitPrincipalProvider {

  private RateLimiterConfiguration rateLimiterConfiguration;

  public StaticRateLimitPrincipalProvider(RateLimiterConfiguration rateLimiterConfiguration) {
    this.rateLimiterConfiguration = rateLimiterConfiguration;
  }

  @Override
  public RateLimitPrincipal getPrincipal(String name, String sourceApp) {
    return new RateLimitPrincipal(
        name,
        overrideOrDefault(
            name,
            rateLimiterConfiguration.getRateSecondsByPrincipal(),
            rateLimiterConfiguration.getRateSeconds()),
        overrideOrDefault(
            name,
            rateLimiterConfiguration.getCapacityByPrincipal(),
            rateLimiterConfiguration.getCapacity()),
        isLearning(
            name,
            rateLimiterConfiguration.getEnforcing(),
            rateLimiterConfiguration.getIgnoring(),
            rateLimiterConfiguration.isLearning()));
  }
}
