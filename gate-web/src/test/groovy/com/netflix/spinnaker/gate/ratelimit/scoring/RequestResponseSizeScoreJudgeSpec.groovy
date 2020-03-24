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

import com.netflix.spinnaker.kork.dynamicconfig.DynamicConfigService
import org.springframework.web.util.ContentCachingResponseWrapper
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import javax.servlet.http.HttpServletRequest

class RequestResponseSizeScoreJudgeSpec extends Specification {

  DynamicConfigService dynamicConfigService = Mock()

  @Subject
  ScoreJudge subject = new RequestResponseSizeScoreJudge(dynamicConfigService)

  @Unroll
  def "calculates score"() {
    given:
    def request = Mock(HttpServletRequest)
    def response = Mock(ContentCachingResponseWrapper)

    when:
    def result = subject.score(request, response)

    then:
    1 * dynamicConfigService.getConfig(_, _, _) >> (double) baselineBytes
    1 * request.getContentLength() >> requestSize
    1 * response.getContentSize() >> responseSize
    result == expected

    where:
    baselineBytes | requestSize | responseSize || expected
    1024          | -1          | 7 * 1024     || 6
    1024 * 1024   | -1          | 2811794      || 2
  }
}
