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

package com.netflix.spinnaker.gate.ratelimit.scoring

import com.netflix.spinnaker.gate.ratelimit.Rate
import com.netflix.spinnaker.gate.ratelimit.RateLimitPrincipal
import com.netflix.spinnaker.kork.jedis.EmbeddedRedis
import com.netflix.spinnaker.kork.test.time.MutableClock
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.util.Pool
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.util.function.Consumer
import java.util.function.Function

class ScoringRedisRateLimiterSpec extends Specification {

  private static final String HANDLER_NAME = "myHandlerName(String, boolean)"

  static int port

  @Shared
  @AutoCleanup("destroy")
  EmbeddedRedis embeddedRedis

  def setupSpec() {
    embeddedRedis = EmbeddedRedis.embed()
    embeddedRedis.jedis.flushDB()
    port = embeddedRedis.port
  }

  def cleanup() {
    embeddedRedis.jedis.flushDB()
  }

  def 'should create new sliding window for unknown principal'() {
    given:
    ScoreJudge judge = Mock() {
      historicalScore(_) >> 2
    }
    ScoringRateLimiter subject = new ScoringRedisRateLimiter((JedisPool) embeddedRedis.pool, judge, new MutableClock())

    and:
    RateLimitPrincipal principal = new RateLimitPrincipal("user@example.com", 10, 10, true)

    and:
    HttpServletRequest request = Mock()

    when:
    Rate rate = subject.incrementRate(principal, request, HANDLER_NAME)

    then:
    noExceptionThrown()
    rate.capacity == 10
    rate.remaining == 8
    !rate.isThrottled()

    and: 'cost of request should be added to sliding window'
    withJedis(embeddedRedis.pool) {
      it.zrange(ScoringRedisRateLimiter.principalKey(principal.name), 0, -1) == ["2"] as Set
    }
  }

  def 'should use historical scores to get current request cost'() {
    given:
    ScoreJudge judge = Mock()
    ScoringRateLimiter subject = new ScoringRedisRateLimiter((JedisPool) embeddedRedis.pool, judge, new MutableClock())

    and:
    RateLimitPrincipal principal = new RateLimitPrincipal("user@example.com", 10, 10, true)

    and:
    withJedis(embeddedRedis.pool) {
      it.rpush(ScoringRedisRateLimiter.handlerKey(HANDLER_NAME), "5")
    }

    and:
    HttpServletRequest request = Mock()

    when:
    Rate rate = subject.incrementRate(principal, request, HANDLER_NAME)

    then:
    noExceptionThrown()
    1 * judge.historicalScore([5]) >> 2
    rate.capacity == 10
    rate.remaining == 8
  }

  def 'should age-out old requests'() {
    given:
    Clock clock = new MutableClock()
    clock.instant(Instant.now())

    ScoreJudge judge = Mock() {
      historicalScore(_) >> 1
    }
    ScoringRateLimiter subject = new ScoringRedisRateLimiter((JedisPool) embeddedRedis.pool, judge, clock)

    and:
    RateLimitPrincipal principal = new RateLimitPrincipal("user@example.com", 10, 10, true)

    and:
    HttpServletRequest request = Mock()

    and:
    subject.incrementRate(principal, request, HANDLER_NAME)
    clock.incrementBy(Duration.ofMinutes(1))

    subject.incrementRate(principal, request, HANDLER_NAME)
    clock.incrementBy(Duration.ofMinutes(30))

    subject.incrementRate(principal, request, HANDLER_NAME)
    clock.incrementBy(Duration.ofMinutes(30))

    when:
    subject.incrementRate(principal, request, HANDLER_NAME)

    then:
    withJedis(embeddedRedis.pool) {
      it.zrange(ScoringRedisRateLimiter.principalKey(principal.name), 0, -1) == ["1"] as Set
    }
  }

  def 'should add score to historical list'() {
    given:
    ScoreJudge judge = Mock() {
      score(_, _) >> 1
    }
    ScoringRateLimiter subject = new ScoringRedisRateLimiter((JedisPool) embeddedRedis.pool, judge, new MutableClock())

    and:
    HttpServletRequest request = Mock()
    HttpServletResponse response = Mock()

    when:
    subject.calculateTotalCost(request, response, HANDLER_NAME)

    then:
    noExceptionThrown()
    withJedis(embeddedRedis.pool) {
      it.lrange(ScoringRedisRateLimiter.handlerKey(HANDLER_NAME), 0, -1) == ["1"]
    }

    when:
    subject.calculateTotalCost(request, response, HANDLER_NAME)

    then:
    noExceptionThrown()
    withJedis(embeddedRedis.pool) {
      it.lrange(ScoringRedisRateLimiter.handlerKey(HANDLER_NAME), 0, -1) == ["1", "1"]
    }
  }

  private static void withJedis(Pool<Jedis> pool, Consumer<Jedis> command) {
    Jedis jedis = pool.getResource()
    try {
      command.accept(jedis)
    } finally {
      jedis.close()
    }
  }
}
