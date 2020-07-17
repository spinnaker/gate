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

@RequestMapping("/oes")
@RestController
@Slf4j
@ConditionalOnExpression('${services.opsmx.enabled:false}')
class OpsmxOesController {
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
  OpsmxOesService opsmxOesService

  @Autowired
  ServiceConfiguration serviceConfiguration

  @Autowired
  OkHttpClient okHttpClient

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}", method = RequestMethod.GET)
  Object getOesResponse(@PathVariable("type") String type,
                        @PathVariable("source") String source,
                        @RequestParam(value = "isTreeView", required = false) boolean isTreeView,
                        @RequestParam(value = "isLatest", required = false) boolean isLatest,
                        @RequestParam(value = "applicationName", required = false) String applicationName,
                        @RequestParam(value = "chartId", required = false) Integer chartId,
                        @RequestParam(value = "imageSource", required = false) String imageSource) {

    return opsmxOesService.getOesResponse(type, source, isTreeView, isLatest,
            applicationName, chartId, imageSource)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}", method = RequestMethod.GET)
  Object getOesResponse4(@PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @PathVariable("source1") String source1) {

    return opsmxOesService.getOesResponse4(type, source, source1)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}", method = RequestMethod.GET)
  Object getOesResponse5(@PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @PathVariable("source1") String source1,
                         @PathVariable("source2") String source2) {

    return opsmxOesService.getOesResponse5(type, source, source1, source2)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.GET)
  Object getOesResponse6(@PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @PathVariable("source1") String source1,
                         @PathVariable("source2") String source2,
                         @PathVariable("source3") String source3) {

    return opsmxOesService.getOesResponse6(type, source, source1, source2, source3)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}", method = RequestMethod.DELETE)
  Object deleteOesResponse(@PathVariable("type") String type,
                           @PathVariable("source") String source) {

    return opsmxOesService.deleteOesResponse(type, source)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}", method = RequestMethod.DELETE)
  Object deleteOesResponse4(@PathVariable("type") String type,
                            @PathVariable("source") String source,
                            @PathVariable("source1") String source1) {

    return opsmxOesService.deleteOesResponse4(type, source, source1)
  }

  @ApiOperation(value = "Add or Update dynamic account configured in Spinnaker", response = String.class)
  @RequestMapping(value = "/addOrUpdateDynamicAccount", method = RequestMethod.POST)
  String addOrUpdateAccount(@RequestParam MultipartFile files, @RequestParam Map<String, String> postData) {
    String filename = files ? files.getOriginalFilename() : ''
    return addOrUpdateDynamicAccount(files.bytes, filename, postData.get("postData"))
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}", method = RequestMethod.POST)
  Object postOesResponse(@PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @RequestParam(value = "isTreeView", required = false) boolean isTreeView,
                         @RequestParam(value = "isLatest", required = false) boolean isLatest,
                         @RequestBody(required = false) Object data) {

    return opsmxOesService.postOesResponse(type, source, isTreeView, isLatest, data)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}", method = RequestMethod.POST)
  Object postOesResponse4(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @RequestBody(required = false) Object data) {

    return opsmxOesService.postOesResponse4(type, source, source1, data)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}", method = RequestMethod.POST)
  Object postOesResponse5(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                          @RequestBody(required = false) Object data) {

    return opsmxOesService.postOesResponse5(type, source, source1, source2, data)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.POST)
  Object postOesResponse6(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                          @PathVariable("source3") String source3,
                          @RequestBody(required = false) Object data) {

    return opsmxOesService.postOesResponse6(type, source, source1, source2, source3, data)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}", method = RequestMethod.PUT)
  Object updateOesResponse(@PathVariable("type") String type,
                           @PathVariable("source") String source,
                           @RequestBody Object data) {

    return opsmxOesService.updateOesResponse(type, source, data)
  }

  @ApiOperation(value = "Endpoint for Oes rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}", method = RequestMethod.PUT)
  Object updateOesResponse4(@PathVariable("type") String type,
                            @PathVariable("source") String source,
                            @PathVariable("source1") String source1,
                            @RequestBody Object data) {

    return opsmxOesService.updateOesResponse4(type, source, source1, data)
  }

  private String addOrUpdateDynamicAccount(byte[] bytes, String filename, String data) {

    AuthenticatedRequest.propagate {
      def request = new Request.Builder()
        .url(serviceConfiguration.getServiceEndpoint("opsmx").url +
          "/oes/accountsConfig/addOrUpdateDynamicAccount")
        .post(new MultipartBody.Builder()
          .addFormDataPart(
            "files",
            filename,
            RequestBody.create(MediaType.parse("application/octet-stream"), bytes))
          .addFormDataPart(
            "postData",
            null,
            RequestBody.create(MediaType.parse("text/plain"), data))
          .build())
        .build()
      def response = okHttpClient.newCall(request).execute()
      return response.body()?.string() ?: "Unknown reason: " + response.code()
    }.call() as String
  }
}
