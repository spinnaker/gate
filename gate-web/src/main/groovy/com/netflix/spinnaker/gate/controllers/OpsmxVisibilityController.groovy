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
import com.netflix.spinnaker.gate.model.ApprovalGateTriggerResponseModel
import com.netflix.spinnaker.gate.services.internal.OpsmxVisibilityService
import groovy.util.logging.Slf4j
import io.swagger.annotations.ApiOperation
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import retrofit.client.Response
import org.apache.commons.io.IOUtils
import org.springframework.http.HttpStatus

@RequestMapping("/visibilityservice")
@RestController
@Slf4j
@ConditionalOnExpression('${services.visibility.enabled:false}')
class OpsmxVisibilityController {
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
  OpsmxVisibilityService opsmxVisibilityService

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/v1/approvalGates/{id}/trigger", method = RequestMethod.POST)
  @ResponseBody Object triggerV1ApprovalGate(@PathVariable("id") Integer id,
                                             @RequestBody(required = false) Object data) {
    Response response = opsmxVisibilityService.triggerV1ApprovalGate(id, data)
    InputStream inputStream = null
    try {
      HttpHeaders headers = new HttpHeaders()
      response.getHeaders().forEach({ header ->
        headers.add(header.getName(), header.getValue())
      })
      if (response.getBody()!=null){
        inputStream = response.getBody().in()
      } else {
        return new ResponseEntity(headers, HttpStatus.valueOf(response.getStatus()))
      }
      String responseBody = new String(IOUtils.toByteArray(inputStream))
      if (responseBody == null || (responseBody!=null && responseBody.trim().isEmpty())){
        return new ResponseEntity(headers, HttpStatus.valueOf(response.getStatus()))
      }
      return new ResponseEntity(responseBody, headers, HttpStatus.valueOf(response.getStatus()))
    } finally{
      if (inputStream!=null){
        inputStream.close()
      }
    }
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/v2/approvalGates/{id}/trigger", method = RequestMethod.POST)
  @ResponseBody Object triggerV2ApprovalGate(@PathVariable("id") Integer id,
                                             @RequestBody(required = false) Object data) {

    Response response = opsmxVisibilityService.triggerV2ApprovalGate(id, data)
    InputStream inputStream = null
    try {
      HttpHeaders headers = new HttpHeaders()
      response.getHeaders().forEach({ header ->
        headers.add(header.getName(), header.getValue())
      })

      inputStream = response.getBody().in()
      //BufferedReader br = null
      StringBuilder sb = new StringBuilder()
      String line
      BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))
      while ((line = br.readLine()) != null) {
        sb.append(line)
        sb.append('\n')
      }
      //inputStream = response.getBody().in()
      //String responseBody = new String(IOUtils.toByteArray(inputStream))
      //ApprovalGateTriggerResponseModel responseBody = response.getBody().asType(ApprovalGateTriggerResponseModel.class)
      String responseBody = sb.toString()
      return new ResponseEntity(responseBody, headers, HttpStatus.valueOf(response.getStatus()))
    } finally{
      if (inputStream!=null){
        inputStream.close()
      }
    }
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.GET)
  Object getVisibilityResponse1(@PathVariable("version") String version,
                                @PathVariable("type") String type,
                                @RequestParam(value = "serviceId", required = false) Integer serviceId) {
    return opsmxVisibilityService.getVisibilityResponse1(version, type, serviceId)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.GET)
  Object getVisibilityResponse(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @RequestParam(value = "source1", required = false) String source1) {
    return opsmxVisibilityService.getVisibilityResponse(version, type, source, source1)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.GET)
  Object getVisibilityResponse4(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @PathVariable("source") String source,
                              @PathVariable("source1") String source1,
                              @RequestParam(value = "status", required = false) String status) {

    return opsmxVisibilityService.getVisibilityResponse4(version, type, source, source1, status)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.GET)
  Object getVisibilityResponse5(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @PathVariable("source") String source,
                              @PathVariable("source1") String source1,
                              @PathVariable("source2") String source2) {

    return opsmxVisibilityService.getVisibilityResponse5(version, type, source, source1, source2)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.GET)
  Object getVisibilityResponse6(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @PathVariable("source") String source,
                              @PathVariable("source1") String source1,
                              @PathVariable("source2") String source2,
                              @PathVariable("source3") String source3) {

    return opsmxVisibilityService.getVisibilityResponse6(version, type, source, source1, source2, source3)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.GET)
  Object getVisibilityResponse7(@PathVariable("version") String version,
                                @PathVariable("type") String type,
                                @PathVariable("source") String source,
                                @PathVariable("source1") String source1,
                                @PathVariable("source2") String source2,
                                @PathVariable("source3") String source3,
                                @PathVariable("source4") String source4) {

    return opsmxVisibilityService.getVisibilityResponse7(version, type, source, source1, source2, source3, source4)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}", method = RequestMethod.GET)
  Object getVisibilityResponse8(@PathVariable("version") String version,
                                @PathVariable("type") String type,
                                @PathVariable("source") String source,
                                @PathVariable("source1") String source1,
                                @PathVariable("source2") String source2,
                                @PathVariable("source3") String source3,
                                @PathVariable("source4") String source4,
                                @PathVariable("source5") String source5) {

    return opsmxVisibilityService.getVisibilityResponse8(version, type, source, source1, source2, source3, source4, source5)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.DELETE)
  Object deleteVisibilityResponse(@PathVariable("version") String version,
                                @PathVariable("type") String type) {

    return opsmxVisibilityService.deleteVisibilityResponse(version, type)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.DELETE)
  Object deleteVisibilityResponse1(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source) {

    return opsmxVisibilityService.deleteVisibilityResponse1(version, type, source)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.DELETE)
  Object deleteVisibilityResponse4(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                   @RequestParam(value = "datasourceName", required = false) String datasourceName) {
    return opsmxVisibilityService.deleteVisibilityResponse4(version, type, source, source1, datasourceName)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.DELETE)
  Object deleteVisibilityResponse5(@PathVariable("version") String version,
                                   @PathVariable("type") String type,
                                   @PathVariable("source") String source,
                                   @PathVariable("source1") String source1,
                                   @PathVariable("source2") String source2) {

    return opsmxVisibilityService.deleteVisibilityResponse5(version, type, source, source1, source2)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.DELETE)
  Object deleteVisibilityResponse6(@PathVariable("version") String version,
                                   @PathVariable("type") String type,
                                   @PathVariable("source") String source,
                                   @PathVariable("source1") String source1,
                                   @PathVariable("source2") String source2,
                                   @PathVariable("source3") String source3) {

    return opsmxVisibilityService.deleteVisibilityResponse6(version, type, source, source1, source2, source3)
  }

  @ApiOperation(value = "Endpoint for platform visibility services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.POST)
  Object postVisibilityResponse(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @RequestBody(required = false) Object data) {

    return opsmxVisibilityService.postVisibilityResponse(version, type,data)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.POST)
  Object postVisibilityResponse3(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @RequestBody(required = false) Object data) {

    return opsmxVisibilityService.postVisibilityResponse3(version, type, source, data)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.POST)
  Object postVisibilityResponse4(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @PathVariable("source1") String source1,
                               @RequestBody(required = false) Object data) {

    return opsmxVisibilityService.postVisibilityResponse4(version, type, source, source1, data)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.POST)
  Object postVisibilityResponse5(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @PathVariable("source1") String source1,
                               @PathVariable("source2") String source2,
                               @RequestBody(required = false) Object data) {

    return opsmxVisibilityService.postVisibilityResponse5(version, type, source, source1, source2, data)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.POST)
  Object postVisibilityResponse6(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @PathVariable("source1") String source1,
                               @PathVariable("source2") String source2,
                               @PathVariable("source3") String source3,
                               @RequestBody(required = false) Object data) {

    return opsmxVisibilityService.postVisibilityResponse6(version, type, source, source1, source2, source3, data)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.POST)
  Object postVisibilityResponse7(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @PathVariable("source3") String source3,
                                 @PathVariable("source4") String source4,
                                 @RequestBody(required = false) Object data) {

    return opsmxVisibilityService.postVisibilityResponse7(version, type, source, source1, source2, source3, source4, data)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}", method = RequestMethod.POST)
  Object postVisibilityResponse8(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @PathVariable("source3") String source3,
                                 @PathVariable("source4") String source4,
                                 @PathVariable("source5") String source5,
                                 @RequestBody(required = false) Object data) {

    return opsmxVisibilityService.postVisibilityResponse8(version, type, source, source1, source2, source3, source4, source5, data)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.PUT)
  Object updateVisibilityResponse(@PathVariable("version") String version,
                                @PathVariable("type") String type,
                                @RequestBody(required = false) Object data) {

    return opsmxVisibilityService.updateVisibilityResponse(version, type, data)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.PUT)
  Object updateVisibilityResponse1(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @RequestBody(required = false) Object data) {

    return opsmxVisibilityService.updateVisibilityResponse1(version, type, source, data)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.PUT)
  Object updateVisibilityResponse2(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @RequestBody(required = false) Object data) {

    return opsmxVisibilityService.updateVisibilityResponse2(version, type, source, source1, data)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.PUT)
  Object updateVisibilityResponse3(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @RequestBody(required = false) Object data) {

    return opsmxVisibilityService.updateVisibilityResponse3(version, type, source, source1, source2, data)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.PUT)
  Object updateVisibilityResponse4(@PathVariable("version") String version,
                                   @PathVariable("type") String type,
                                   @PathVariable("source") String source,
                                   @PathVariable("source1") String source1,
                                   @PathVariable("source2") String source2,
                                   @PathVariable("source3") String source3,
                                   @RequestBody(required = false) Object data) {

    return opsmxVisibilityService.updateVisibilityResponse4(version, type, source, source1, source2, source3, data)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.PUT)
  Object updateVisibilityResponse5(@PathVariable("version") String version,
                                   @PathVariable("type") String type,
                                   @PathVariable("source") String source,
                                   @PathVariable("source1") String source1,
                                   @PathVariable("source2") String source2,
                                   @PathVariable("source3") String source3,
                                   @PathVariable("source4") String source4,
                                   @RequestBody(required = false) Object data) {

    return opsmxVisibilityService.updateVisibilityResponse5(version, type, source, source1, source2, source3, source4, data)
  }

  @ApiOperation(value = "Endpoint for visibility rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}", method = RequestMethod.PUT)
  Object updateVisibilityResponse6(@PathVariable("version") String version,
                                   @PathVariable("type") String type,
                                   @PathVariable("source") String source,
                                   @PathVariable("source1") String source1,
                                   @PathVariable("source2") String source2,
                                   @PathVariable("source3") String source3,
                                   @PathVariable("source4") String source4,
                                   @PathVariable("source5") String source5,
                                   @RequestBody(required = false) Object data) {

    return opsmxVisibilityService.updateVisibilityResponse5(version, type, source, source1, source2, source3, source4, source5, data)
  }
}
