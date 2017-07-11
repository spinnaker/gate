/*
 * Copyright 2017 Google, Inc.
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

package com.netflix.spinnaker.gate.controllers

import com.netflix.spinnaker.gate.exceptions.NotFoundException
import com.netflix.spinnaker.gate.services.CanaryConfigService
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
class V2CanaryController {
  @Autowired(required = false)
  CanaryConfigService canaryConfigService

  @ApiOperation(value = "Retrieve a list of canary configurations")
  @RequestMapping(value = "/v2/canaryConfig", method = RequestMethod.GET)
  List getCanaryConfigs() {
    if (canaryConfigService) {
      return canaryConfigService.getCanaryConfigs()
    } else {
      return []
    }
  }

  @ApiOperation(value = "Retrieve a canary configuration by name")
  @RequestMapping(value = "/v2/canaryConfig/{id}", method = RequestMethod.GET)
  Map getCanaryConfig(@PathVariable String id) {
    if (canaryConfigService) {
      return canaryConfigService.getCanaryConfig(id)
    } else {
      throw new NotFoundException("Canary configuration not found (id: ${id})")
    }
  }
}
