/*
 * Copyright 2014 Netflix, Inc.
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

import com.netflix.spinnaker.gate.services.commands.HystrixFactory
import com.netflix.spinnaker.gate.services.internal.Front50Service
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseStatus

@CompileStatic
@Component
@Slf4j
class StrategyService {
  private static final String GROUP = "strategies"

  @Autowired(required = false)
  Front50Service front50Service

  @Autowired
  ApplicationService applicationService

  void deleteForApplication(String applicationName, String strategyName) {
    front50Service.deleteStrategyConfig(applicationName, strategyName)
  }

  void save(Map strategy) {
    front50Service.saveStrategyConfig(strategy)
  }

  Map update(String strategyId, Map strategy) {
    HystrixFactory.newMapCommand(GROUP, "updateStrategy") {
      front50Service.updateStrategy(strategyId, strategy)
    } execute()
  }

  void move(Map moveCommand) {
    front50Service.moveStrategyConfig(moveCommand)
  }
}
