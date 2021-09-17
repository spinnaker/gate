/*
 * Copyright 2021 OpsMx, Inc.
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

package com.opsmx.spinnaker.gate.controllers

import com.opsmx.spinnaker.gate.services.OpsmxAuditClientService
import groovy.util.logging.Slf4j
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/auditclientservice")
@RestController
@Slf4j
@ConditionalOnExpression('${services.auditclient.enabled:false}')
class OpsmxAuditClientServiceController {

  @Autowired
  OpsmxAuditClientService opsmxAuditClientService

  @ApiOperation(value = "Endpoint for audit-client rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.GET)
  Object getAuditClientResponse1(@PathVariable("version") String version,
                              @PathVariable("type") String type) {
    return opsmxAuditClientService.getAuditClientResponse1(version, type)
  }

  @ApiOperation(value = "Endpoint for audit-client rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.GET)
  Object getAuditClientResponse2(@PathVariable("version") String version,
                             @PathVariable("type") String type,
                             @PathVariable("source") String source) {
    return opsmxAuditClientService.getAuditClientResponse2(version, type, source)
  }

  @ApiOperation(value = "Endpoint for audit-client rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.GET)
  Object getAuditClientResponse3(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @PathVariable("source") String source,
                              @PathVariable("source1") String source1) {

    return opsmxAuditClientService.getAuditClientResponse3(version, type, source, source1)
  }

  @ApiOperation(value = "Endpoint for audit-client rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.GET)
  Object getAuditClientResponse4(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @PathVariable("source") String source,
                              @PathVariable("source1") String source1,
                              @PathVariable("source2") String source2) {

    return opsmxAuditClientService.getAuditClientResponse4(version, type, source, source1, source2)
  }

  @ApiOperation(value = "Endpoint for audit-client rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.GET)
  Object getAuditClientResponse5(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @PathVariable("source") String source,
                              @PathVariable("source1") String source1,
                              @PathVariable("source2") String source2,
                              @PathVariable("source3") String source3) {

    return opsmxAuditClientService.getAuditClientResponse5(version, type, source, source1, source2, source3)
  }

  @ApiOperation(value = "Endpoint for audit-client rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.GET)
  Object getAuditClientResponse6(@PathVariable("version") String version,
                              @PathVariable("type") String type,
                              @PathVariable("source") String source,
                              @PathVariable("source1") String source1,
                              @PathVariable("source2") String source2,
                              @PathVariable("source3") String source3,
                              @PathVariable("source4") String source4) {

    return opsmxAuditClientService.getAuditClientResponse6(version, type, source, source1, source2, source3, source4)
  }

  @ApiOperation(value = "Endpoint for audit-client rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}", method = RequestMethod.GET)
  Object getAuditClientResponse7(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @PathVariable("source3") String source3,
                                 @PathVariable("source4") String source4,
                                 @PathVariable("source4") String source5) {

    return opsmxAuditClientService.getAuditClientResponse7(version, type, source, source1, source2, source3, source4, source5)
  }
}
