/*
 * Copyright 2024 Netflix, Inc.
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

package com.netflix.spinnaker.gate.services


import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import com.netflix.spinnaker.kork.jedis.EmbeddedRedis
import spock.lang.Unroll

class SessionServiceSpec extends Specification {
  @Shared
  @AutoCleanup("destroy")
  EmbeddedRedis embeddedRedis

  def setupSpec() {
    embeddedRedis = EmbeddedRedis.embed()
  }

  @Unroll
  def "should delete Spring sessions"() {
    given:
    def jedis = embeddedRedis.jedis
    jedis.set("spring:session:session1", "session1-data")
    jedis.set("spring:session:session2", "session2-data")
    jedis.set("other:key", "other-data")

    def subject = new SessionService()
    subject.jedisPool = embeddedRedis.pool

    when:
    subject.deleteSpringSessions()

    then:
    jedis.keys("spring:session*").size() == 0
    jedis.keys("other:key").size() == 1
  }
}
