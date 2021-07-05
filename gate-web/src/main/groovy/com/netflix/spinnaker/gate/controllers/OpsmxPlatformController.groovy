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
import com.netflix.spinnaker.gate.services.internal.OpsmxPlatformService
import groovy.util.logging.Slf4j
import io.swagger.annotations.ApiOperation
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.web.bind.annotation.*

@RequestMapping("/platformservice")
@RestController
@Slf4j
@ConditionalOnExpression('${services.platform.enabled:false}')
class OpsmxPlatformController {
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
  OpsmxPlatformService opsmxPlatformService

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.GET)
  Object getPlatformResponse1(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @RequestParam(value = "datasourceType", required = false) String datasourceType,
                              @RequestParam(value = "accountName", required = false) String accountName) {
    return opsmxPlatformService.getPlatformResponse1(version, type, datasourceType, accountName)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.GET)
  Object getPlatformResponse(@PathVariable("version") String version,
                         @PathVariable("type") String type,
                         @PathVariable("source") String source,
                             @RequestParam(value = "source1", required = false) String source1) {
    return opsmxPlatformService.getPlatformResponse(version, type, source, source1)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.GET)
  Object getPlatformResponse4(@PathVariable("version") String version,
                          @PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @RequestParam(value = "datasourceType", required = false) String datasourceType,
                          @RequestParam(value = "permissionId", required = false) String permissionId) {

    return opsmxPlatformService.getPlatformResponse4(version, type, source, source1,datasourceType, permissionId)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.GET)
  Object getPlatformResponse5(@PathVariable("version") String version,
                          @PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                          @RequestParam(value = "permissionId", required = false) String permissionId,
                          @RequestParam(value = "resourceType", required = false) String resourceType) {

    return opsmxPlatformService.getPlatformResponse5(version, type, source, source1, source2, permissionId, resourceType)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.GET)
  Object getPlatformResponse6(@PathVariable("version") String version,
                          @PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                          @PathVariable("source3") String source3) {

    return opsmxPlatformService.getPlatformResponse6(version, type, source, source1, source2, source3)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.GET)
  Object getPlatformResponse7(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @PathVariable("source") String source,
                              @PathVariable("source1") String source1,
                              @PathVariable("source2") String source2,
                              @PathVariable("source3") String source3,
                              @PathVariable("source4") String source4) {

    return opsmxPlatformService.getPlatformResponse7(version, type, source, source1, source2, source3, source4)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.DELETE)
  Object deletePlatformResponse(@PathVariable("version") String version,
                                @PathVariable("type") String type,
                                @RequestParam(value = "accountName", required = false) String accountName) {
    return opsmxPlatformService.deletePlatformResponse(version, type, accountName)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.DELETE)
  Object deletePlatformResponse1(@PathVariable("version") String version,
                            @PathVariable("type") String type,
                            @PathVariable("source") String source) {

    return opsmxPlatformService.deletePlatformResponse1(version, type, source)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.DELETE)
  Object deletePlatformResponse4(@PathVariable("version") String version,
                             @PathVariable("type") String type,
                             @PathVariable("source") String source,
                             @PathVariable("source1") String source1) {

    return opsmxPlatformService.deletePlatformResponse4(version, type, source, source1)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.POST)
  Object postPlatformResponse(@PathVariable("version") String version,
                          @PathVariable("type") String type,
                          @RequestBody(required = false) Object data) {

    return opsmxPlatformService.postPlatformResponse(version, type,data)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.POST)
  Object postPlatformResponse3(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @RequestBody(required = false) Object data) {

    return opsmxPlatformService.postPlatformResponse3(version, type, source, data)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.POST)
  Object postPlatformResponse4(@PathVariable("version") String version,
                           @PathVariable("type") String type,
                           @PathVariable("source") String source,
                           @PathVariable("source1") String source1,
                           @RequestBody(required = false) Object data) {

    return opsmxPlatformService.postPlatformResponse4(version, type, source, source1, data)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.POST)
  Object postPlatformResponse5(@PathVariable("version") String version,
                           @PathVariable("type") String type,
                           @PathVariable("source") String source,
                           @PathVariable("source1") String source1,
                           @PathVariable("source2") String source2,
                           @RequestBody(required = false) Object data) {

    return opsmxPlatformService.postPlatformResponse5(version, type, source, source1, source2, data)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.POST)
  Object postPlatformResponse6(@PathVariable("version") String version,
                           @PathVariable("type") String type,
                           @PathVariable("source") String source,
                           @PathVariable("source1") String source1,
                           @PathVariable("source2") String source2,
                           @PathVariable("source3") String source3,
                           @RequestBody(required = false) Object data) {

    return opsmxPlatformService.postPlatformResponse6(version, type, source, source1, source2, source3, data)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.PUT)
  Object updatePlatformResponse(@PathVariable("version") String version,
                           @PathVariable("type") String type,
                           @RequestBody(required = false) Object data) {

    return opsmxPlatformService.updatePlatformResponse(version, type, data)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.PUT)
  Object updatePlatformResponse1(@PathVariable("version") String version,
                                @PathVariable("type") String type,
                                @PathVariable("source") String source,
                                @RequestBody(required = false) Object data) {

    return opsmxPlatformService.updatePlatformResponse1(version, type, source, data)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.PUT)
  Object updatePlatformResponse2(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @RequestBody(required = false) Object data) {

    return opsmxPlatformService.updatePlatformResponse2(version, type, source, source1, data)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.PUT)
  Object updatePlatformResponse3(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @RequestBody(required = false) Object data) {

    return opsmxPlatformService.updatePlatformResponse3(version, type, source, source1, source2, data)
  }

  @ApiOperation(value = "Endpoint for platform rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.PUT)
  Object updatePlatformResponse4(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @PathVariable("source") String source,
                              @PathVariable("source1") String source1,
                              @PathVariable("source2") String source2,
                              @PathVariable("source3") String source3,
                              @PathVariable("source4") String source4,
                                 @RequestBody(required = false) Object data) {

    return opsmxPlatformService.updatePlatformResponse4(version, type, source, source1, source2, source3, source4, data)
  }
}
