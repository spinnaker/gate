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
package com.netflix.spinnaker.gate.config;

import com.netflix.spinnaker.gate.ratelimit.BucketedRedisRateLimiter;
import com.netflix.spinnaker.gate.ratelimit.RateLimitPrincipalProvider;
import com.netflix.spinnaker.gate.ratelimit.RateLimiter;
import com.netflix.spinnaker.gate.ratelimit.RedisRateLimitPrincipalProvider;
import com.netflix.spinnaker.gate.ratelimit.StaticRateLimitPrincipalProvider;
import com.netflix.spinnaker.gate.ratelimit.scoring.RequestResponseSizeScoreJudge;
import com.netflix.spinnaker.gate.ratelimit.scoring.ScoreJudge;
import com.netflix.spinnaker.gate.ratelimit.scoring.ScoringRedisRateLimiter;
import com.netflix.spinnaker.kork.dynamicconfig.DynamicConfigService;
import java.time.Clock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;

@Configuration
@ConditionalOnProperty("rate-limit.enabled")
public class RateLimiterConfig {

  @Autowired(required = false)
  RateLimiterConfigProperties rateLimiterConfiguration;

  @Bean
  @ConditionalOnMissingBean(Clock.class)
  Clock clock() {
    return Clock.systemDefaultZone();
  }

  @Bean
  @ConditionalOnExpression("${rate-limit.redis.enabled:false} && ${rate-limit.redis.bucketed:true}")
  RateLimiter redisRateLimiter(JedisPool jedisPool) {
    return new BucketedRedisRateLimiter(jedisPool);
  }

  @Bean
  @ConditionalOnExpression("${rate-limit.redis.enabled:false} && ${rate-limit.redis.scoring:false}")
  @ConditionalOnMissingBean(ScoreJudge.class)
  ScoreJudge requestResponseSizeScoreJudge(DynamicConfigService dynamicConfigService) {
    return new RequestResponseSizeScoreJudge(dynamicConfigService);
  }

  @Bean
  @ConditionalOnExpression("${rate-limit.redis.enabled:false} && ${rate-limit.redis.scoring:false}")
  RateLimiter redisScoringRateLimiter(JedisPool jedisPool, ScoreJudge scoreJudge, Clock clock) {
    return new ScoringRedisRateLimiter(jedisPool, scoreJudge, clock);
  }

  @Bean
  @ConditionalOnExpression(
      "${rate-limit.redis.enabled:false} && ${rate-limit.redis.principal-provider:true}")
  RateLimitPrincipalProvider redisRateLimiterPrincipalProvider(JedisPool jedisPool) {
    return new RedisRateLimitPrincipalProvider(jedisPool, rateLimiterConfiguration);
  }

  @Bean
  @ConditionalOnMissingBean(RateLimitPrincipalProvider.class)
  RateLimitPrincipalProvider staticRateLimiterPrincipalProvider() {
    return new StaticRateLimitPrincipalProvider(rateLimiterConfiguration);
  }
}
