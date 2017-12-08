/*
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import com.netflix.spinnaker.gate.services.internal.KeelService
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@ConditionalOnProperty('services.keel.enabled')
@CompileStatic
@Component
@Slf4j
class IntentService {

  @Autowired
  KeelService keelService

  // TODO eb: Hystrix-ify
  Map getIntent(String intentId) {
    return keelService.getIntent(intentId)
  }

  List<Map> getIntents(List<String> status) {
    return keelService.getIntents(status)
  }

  Map deleteIntent(String intentId) {
    return keelService.deleteIntent(intentId)
  }

  List<Map> upsertIntent(Map upsertIntentRequest) {
    return keelService.upsertIntent(upsertIntentRequest)
  }

  List<String> getHistory(String intentId) {
    return keelService.getHistory(intentId)
  }

  List<Map> getTraces(String intentId) {
    return keelService.getTraces(intentId)
  }
}
