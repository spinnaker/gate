/*
 * Copyright 2015 Netflix, Inc.
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

package com.netflix.spinnaker.gate.services

import com.netflix.spinnaker.gate.services.internal.EchoService
import com.netflix.spinnaker.kork.retrofit.exceptions.SpinnakerServerException
import com.netflix.spinnaker.kork.retrofit.exceptions.UpstreamBadRequest
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@CompileStatic
@Slf4j
class CronService {
  @Autowired(required=false)
  EchoService echoService

  Map validateCronExpression(String cronExpression) {
    if (!echoService) {
      return [ valid: false, message: 'No echo service available' ]
    }

    try {
      Map validationResult = echoService.validateCronExpression(cronExpression)
      return [ valid: true, description: validationResult.description ]
    } catch (SpinnakerServerException e) {
      Throwable cause = e.getCause()
      if (!(cause instanceof UpstreamBadRequest)) {
        throw e
      }

      UpstreamBadRequest upstreamBadRequest = (UpstreamBadRequest) cause
      if (upstreamBadRequest.status == 400 && upstreamBadRequest.error instanceof Map) {
        return [ valid: false, message: ((Map)upstreamBadRequest.error).message ]
      }
      throw e
    }
  }
}
