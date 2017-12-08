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

package com.netflix.spinnaker.gate.controllers

import com.netflix.spinnaker.gate.services.IntentService
import com.netflix.spinnaker.kork.web.exceptions.NotFoundException
import groovy.util.logging.Slf4j
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@ConditionalOnProperty('services.keel.enabled')
@RestController
@RequestMapping(value = "/intents")
@Slf4j
class IntentController {

  @Autowired
  IntentService intentService

  @ApiOperation(value = "Retrieve an intent")
  @RequestMapping(value = "/{intentId}", method = RequestMethod.GET)
  Map getIntent(@PathVariable(name = "intentId") String intentId) {
    def result = intentService.getIntent(intentId)
    if (!result){
      log.warn("Intent {} not found", value("intent", intentId))
      throw new NotFoundException("Intent not found (id: $intentId)")
    }
    return result
  }

  @ApiOperation(value = "Retrieve a list of intents")
  @RequestMapping(method = RequestMethod.GET)
  List<Map> getIntents(@RequestParam(value = "status", required = false) List<String> status) {
    return intentService.getIntents(status)
  }

  @ApiOperation(value = "Delete an intent")
  @RequestMapping(value = "/{intentId}", method = RequestMethod.DELETE)
  Map deleteIntent(@PathVariable(name = "intentId") String intentId) {
    return intentService.deleteIntent(intentId)
  }

  @ApiOperation(value = "Upsert an intent")
  @RequestMapping(value = "", method = RequestMethod.POST)
  List<Map> upsertIntents(@RequestBody Map upsertIntentRequest) {
    return intentService.upsertIntent(upsertIntentRequest)
  }

  @ApiOperation(value = "Retrieve history for an intent")
  @RequestMapping(value = "/{intentId}/history", method = RequestMethod.GET)
  List<String> getHistory(@PathVariable("intentId") String intentId) {
    return intentService.getHistory(intentId)
  }

  @ApiOperation(value = "Retrieve trace for an intent")
  @RequestMapping(value = "/{intentId}/traces", method = RequestMethod.GET)
  List<Map> getTraces(@PathVariable("intentId") String intentId) {
    return intentService.getTraces(intentId)
  }
}
