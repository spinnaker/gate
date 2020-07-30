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

package com.netflix.spinnaker.gate.controllers

import com.netflix.spinnaker.gate.config.ServiceConfiguration
import com.netflix.spinnaker.gate.services.internal.OpsmxAutopilotService
import com.netflix.spinnaker.gate.services.internal.OpsmxOesService
import com.netflix.spinnaker.security.AuthenticatedRequest
import groovy.util.logging.Slf4j
import io.swagger.annotations.ApiOperation
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/autopilot")
@RestController
@Slf4j
@ConditionalOnExpression('${services.autopilot.enabled:false}')
class OpsmxAutopilotController {
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

  @Autowired
  OpsmxAutopilotService opsmxAutopilotService

  @Autowired
  ServiceConfiguration serviceConfiguration

  @Autowired
  OkHttpClient okHttpClient

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}", method = RequestMethod.GET)
  Object getAutoResponse(@PathVariable("type") String type, @PathVariable("source") String source,
                         @RequestParam(value = "application", required = false) Integer id,
                         @RequestParam(value = "applicationId", required = false) Integer applicationId,
                         @RequestParam(value = "serviceId", required = false) Integer serviceId,
                         @RequestParam(value = "startTime", required = false) Long startTime,
                         @RequestParam(value = "endTime", required = false) Long endTime,
                         @RequestParam(value = "intervalMins", required = false) Float intervalMins,
                         @RequestParam(value = "limit", required = false) Integer limit,
                         @RequestParam(value = "sourceType", required = false) String sourceType,
                         @RequestParam(value = "accountName", required = false)  String accountName,
                         @RequestParam(name = "templateType",required = false) String templateType,
                         @RequestParam(value = "name", required = false) String name,
                         @RequestParam(name = "appId",required = false) Integer appId,
                         @RequestParam(value = "pipelineid", required = false) String pipelineId,
                         @RequestParam(value = "applicationName",required = false) String applicationName,
                         @RequestParam(value = "username", required = false) String userName,
                         @RequestParam(value = "templateName", required = false) String templateName,
                         @RequestParam(value = "credentialType", required = false) String credentialType,
                         @RequestParam(value = "id", required = false) Integer canaryId,
                         @RequestParam(value = "service", required = false) Integer service,
                         @RequestParam(value = "canaryId", required = false) Integer canary,
                         @RequestParam(value = "clusterId", required = false) Long clusterId,
                         @RequestParam(value = "version", required = false) String  version){
    return opsmxAutopilotService.getAutoResponse(type, source, id, applicationId, serviceId, startTime, endTime, intervalMins, limit, sourceType,
      accountName, templateType, name, appId, pipelineId, applicationName, userName, templateName, credentialType, canaryId, service, canary, clusterId, version)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}", method = RequestMethod.GET)
  Object getAutoResponse4(@PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @PathVariable("source1") String source1) {

    return opsmxAutopilotService.getAutoResponse4(type, source, source1)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}", method = RequestMethod.GET)
  Object getAutoResponse5(@PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @PathVariable("source1") String source1,
                         @PathVariable("source2") String source2) {

    return opsmxAutopilotService.getAutoResponse5(type, source, source1, source2)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.GET)
  Object getAutoResponse6(@PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @PathVariable("source1") String source1,
                         @PathVariable("source2") String source2,
                         @PathVariable("source3") String source3) {

    return opsmxAutopilotService.getAutoResponse6(type, source, source1, source2, source3)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}", method = RequestMethod.DELETE)
  Object deleteAutoResponse(@PathVariable("type") String type,
                           @PathVariable("source") String source) {

    return opsmxAutopilotService.deleteAutoResponse(type, source)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}", method = RequestMethod.DELETE)
  Object deleteAutoResponse4(@PathVariable("type") String type,
                            @PathVariable("source") String source,
                            @PathVariable("source1") String source1) {

    return opsmxAutopilotService.deleteAutoResponse4(type, source, source1)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}", method = RequestMethod.POST)
  Object postAutoResponse(@PathVariable("type") String type,
                         @RequestBody(required = false) Object data) {

    return opsmxAutopilotService.postAutoResponse(type,data)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}", method = RequestMethod.POST)
  Object postAutoResponse4(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @RequestBody(required = false) Object data) {

    return opsmxAutopilotService.postAutoResponse4(type, source, source1, data)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}", method = RequestMethod.POST)
  Object postAutoResponse5(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                          @RequestBody(required = false) Object data) {

    return opsmxAutopilotService.postAutoResponse5(type, source, source1, source2, data)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.POST)
  Object postAutoResponse6(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                          @PathVariable("source3") String source3,
                          @RequestBody(required = false) Object data) {

    return opsmxAutopilotService.postAutoResponse6(type, source, source1, source2, source3, data)
  }

}
