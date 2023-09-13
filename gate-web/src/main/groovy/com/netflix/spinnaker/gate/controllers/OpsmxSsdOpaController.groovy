/*
 * Copyright 2023 Netflix, Inc.
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

package com.netflix.spinnaker.gate.controllers

import com.netflix.spinnaker.gate.services.internal.OpsmxSsdOpaService
import groovy.util.logging.Slf4j
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/ssdOpa")
@RestController
@Slf4j
@ConditionalOnExpression('${services.ssdopaservice.enabled:false}')
class OpsmxSsdOpaController {

  @Autowired
  OpsmxSsdOpaService opsmxSsdOpaService



  @ApiOperation(value = "Endpoint for ssd rest services")
  @RequestMapping(value = "/api/{version}/{type}", method = RequestMethod.POST)
  Object postSsdOpaServiceResponse1(@PathVariable("version") String version,
                                    @PathVariable("type") String type,
                                    @RequestBody(required = false) Object data) {
    return opsmxSsdOpaService.postSsdOpaServiceResponse1(version, type, data)
  }

  @ApiOperation(value = "Endpoint for ssd rest services")
  @RequestMapping(value = "{source}/api/{version}/{type}", method = RequestMethod.POST)
  Object postSsdOpaServiceResponse(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @RequestBody(required = false) Object data) {
    return opsmxSsdOpaService.postSddOpaResponse2(version, type,source, data)
  }


  @ApiOperation(value = "Endpoint for ssd services")
  @RequestMapping(value = "/api/{version}/{type}", method = RequestMethod.GET)
  Object getSsdOpaResponse1(@PathVariable("version") String version,
                         @PathVariable("type") String type) {
    return opsmxSsdOpaService.getSddOpaResponse3(version, type)
  }



}



