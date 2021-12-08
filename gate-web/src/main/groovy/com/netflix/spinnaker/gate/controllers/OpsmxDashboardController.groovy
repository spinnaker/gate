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


import com.netflix.spinnaker.gate.services.internal.OpsmxDashboardService
import com.opsmx.spinnaker.gate.service.DashboardService
import groovy.util.logging.Slf4j
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.web.bind.annotation.*

import javax.servlet.http.HttpServletRequest

@RequestMapping("/dashboardservice")
@RestController
@Slf4j
@ConditionalOnExpression('${services.dashboard.enabled:false}')
class OpsmxDashboardController {
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
  OpsmxDashboardService opsmxDashboardService

  @Autowired
  DashboardService dashboardService

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.GET)
  Object getDashboardResponse1(@PathVariable("version") String version,
                             @PathVariable("type") String type, HttpServletRequest httpServletRequest) {

    Object response = null

    String path = httpServletRequest.getRequestURI()
    log.info("path : {}", path)
    if (dashboardService.isRegisteredCachingEndpoint(path)) {
      response = handleCaching(httpServletRequest, version, type, response)
    } else {
      response = opsmxDashboardService.getDashboardResponse1(version, type)
    }

    return response
  }

  private Object handleCaching(HttpServletRequest httpServletRequest, String version, String type, Object response) {
    String userName = httpServletRequest.getUserPrincipal().getName()
    log.info("userName : {}", userName)

    boolean isCacheNotEmpty = dashboardService.isCacheNotEmpty("datasource", userName)
    log.info("isCacheNotEmpty : {}", isCacheNotEmpty)
    if (isCacheNotEmpty) {
      response = dashboardService.fetchResponseFromCache("datasource", userName)
    } else {
      response = opsmxDashboardService.getDashboardResponse1(version, type)
      dashboardService.cacheResponse(response, userName)
    }
    response
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.GET)
  Object getDashboardResponse(@PathVariable("version") String version,
                         @PathVariable("type") String type,
                         @PathVariable("source") String source) {
    return opsmxDashboardService.getDashboardResponse(version, type, source)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.GET)
  Object getDashboardResponse4(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @PathVariable("source1") String source1,
                               @RequestParam(value = "pageNo", required = false) Integer pageNo,
                               @RequestParam(value = "pageLimit", required = false) Integer pageLimit,
                               @RequestParam(value = "sortBy", required = false) String sortBy,
                               @RequestParam(value = "sortOrder", required = false) String sortOrder,
                               @RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "noOfDays", required = false) Integer noOfDays) {

    return opsmxDashboardService.getDashboardResponse4(version, type, source, source1, pageNo, pageLimit, sortBy, sortOrder, search, noOfDays)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.GET)
  Object getDashboardResponse5(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @PathVariable("source1") String source1,
                               @PathVariable("source2") String source2,
                               @RequestParam(value = "pageNo", required = false) Integer pageNo,
                               @RequestParam(value = "pageLimit", required = false) Integer pageLimit,
                               @RequestParam(value = "sortBy", required = false) String sortBy,
                               @RequestParam(value = "sortOrder", required = false) String sortOrder) {

    return opsmxDashboardService.getDashboardResponse5(version, type, source, source1, source2, pageNo, pageLimit, sortBy, sortOrder)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.GET)
  Object getDashboardResponse6(@PathVariable("version") String version,
                          @PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                          @PathVariable("source3") String source3,
                               @RequestParam(value = "noOfDays", required = false) Integer noOfDays) {

    return opsmxDashboardService.getDashboardResponse6(version, type, source, source1, source2, source3, noOfDays)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.GET)
  Object getDashboardResponse7(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @PathVariable("source1") String source1,
                               @PathVariable("source2") String source2,
                               @PathVariable("source3") String source3,
                               @PathVariable("source4") String source4,
                               @RequestParam(value = "pageNo", required = false) Integer pageNo,
                               @RequestParam(value = "pageLimit", required = false) Integer pageLimit,
                               @RequestParam(value = "sortBy", required = false) String sortBy,
                               @RequestParam(value = "sortOrder", required = false) String sortOrder) {

    return opsmxDashboardService.getDashboardResponse7(version, type, source, source1, source2, source3, source4, pageNo, pageLimit, sortBy, sortOrder)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}", method = RequestMethod.GET)
  Object getDashboardResponse8(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @PathVariable("source1") String source1,
                               @PathVariable("source2") String source2,
                               @PathVariable("source3") String source3,
                               @PathVariable("source4") String source4,
                               @PathVariable("source5") String source5) {

    return opsmxDashboardService.getDashboardResponse8(version, type, source, source1, source2, source3, source4, source5)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.DELETE)
  Object deleteDashboardResponse(@PathVariable("version") String version,
                                @PathVariable("type") String type) {

    return opsmxDashboardService.deleteDashboardResponse(version, type)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.DELETE)
  Object deleteDashboardResponse1(@PathVariable("version") String version,
                            @PathVariable("type") String type,
                            @PathVariable("source") String source) {

    return opsmxDashboardService.deleteDashboardResponse1(version, type, source)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.DELETE)
  Object deleteDashboardResponse4(@PathVariable("version") String version,
                             @PathVariable("type") String type,
                             @PathVariable("source") String source,
                             @PathVariable("source1") String source1) {

    return opsmxDashboardService.deleteDashboardResponse4(version, type, source, source1)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.DELETE)
  Object deleteDashboardResponse5(@PathVariable("version") String version,
                                  @PathVariable("type") String type,
                                  @PathVariable("source") String source,
                                  @PathVariable("source1") String source1,
                                  @PathVariable("source2") String source2) {

    return opsmxDashboardService.deleteDashboardResponse5(version, type, source, source1, source2)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.DELETE)
  Object deleteDashboardResponse6(@PathVariable("version") String version,
                                  @PathVariable("type") String type,
                                  @PathVariable("source") String source,
                                  @PathVariable("source1") String source1,
                                  @PathVariable("source2") String source2,
                                  @PathVariable("source3") String source3) {

    return opsmxDashboardService.deleteDashboardResponse6(version, type, source, source1, source2, source3)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.DELETE)
  Object deleteDashboardResponse7(@PathVariable("version") String version,
                                  @PathVariable("type") String type,
                                  @PathVariable("source") String source,
                                  @PathVariable("source1") String source1,
                                  @PathVariable("source2") String source2,
                                  @PathVariable("source3") String source3,
                                  @PathVariable("source4") String source4) {

    return opsmxDashboardService.deleteDashboardResponse7(version, type, source, source1, source2, source3, source4)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}", method = RequestMethod.DELETE)
  Object deleteDashboardResponse8(@PathVariable("version") String version,
                                  @PathVariable("type") String type,
                                  @PathVariable("source") String source,
                                  @PathVariable("source1") String source1,
                                  @PathVariable("source2") String source2,
                                  @PathVariable("source3") String source3,
                                  @PathVariable("source4") String source4,
                                  @PathVariable("source5") String source5) {

    return opsmxDashboardService.deleteDashboardResponse8(version, type, source, source1, source2, source3, source4, source5)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.POST)
  Object postDashboardResponse(@PathVariable("version") String version,
                          @PathVariable("type") String type,
                          @RequestBody(required = false) Object data) {

    return opsmxDashboardService.postDashboardResponse(version, type,data)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.POST)
  Object postDashboardResponse3(@PathVariable("version") String version,
                               @PathVariable("type") String type,
                               @PathVariable("source") String source,
                               @RequestBody(required = false) Object data) {

    return opsmxDashboardService.postDashboardResponse3(version, type, source, data)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.POST)
  Object postDashboardResponse4(@PathVariable("version") String version,
                           @PathVariable("type") String type,
                           @PathVariable("source") String source,
                           @PathVariable("source1") String source1,
                           @RequestBody(required = false) Object data) {

    return opsmxDashboardService.postDashboardResponse4(version, type, source, source1, data)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.POST)
  Object postDashboardResponse5(@PathVariable("version") String version,
                           @PathVariable("type") String type,
                           @PathVariable("source") String source,
                           @PathVariable("source1") String source1,
                           @PathVariable("source2") String source2,
                           @RequestBody(required = false) Object data) {

    return opsmxDashboardService.postDashboardResponse5(version, type, source, source1, source2, data)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.POST)
  Object postDashboardResponse6(@PathVariable("version") String version,
                           @PathVariable("type") String type,
                           @PathVariable("source") String source,
                           @PathVariable("source1") String source1,
                           @PathVariable("source2") String source2,
                           @PathVariable("source3") String source3,
                           @RequestBody(required = false) Object data) {

    return opsmxDashboardService.postDashboardResponse6(version, type, source, source1, source2, source3, data)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.POST)
  Object postDashboardResponse7(@PathVariable("version") String version,
                                @PathVariable("type") String type,
                                @PathVariable("source") String source,
                                @PathVariable("source1") String source1,
                                @PathVariable("source2") String source2,
                                @PathVariable("source3") String source3,
                                @PathVariable("source4") String source4,
                                @RequestBody(required = false) Object data) {

    return opsmxDashboardService.postDashboardResponse7(version, type, source, source1, source2, source3, source4, data)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}", method = RequestMethod.POST)
  Object postDashboardResponse8(@PathVariable("version") String version,
                                @PathVariable("type") String type,
                                @PathVariable("source") String source,
                                @PathVariable("source1") String source1,
                                @PathVariable("source2") String source2,
                                @PathVariable("source3") String source3,
                                @PathVariable("source4") String source4,
                                @PathVariable("source5") String source5,
                                @RequestBody(required = false) Object data) {

    return opsmxDashboardService.postDashboardResponse8(version, type, source, source1, source2, source3, source4, source5, data)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.PUT)
  Object updateDashboardResponse(@PathVariable("version") String version,
                           @PathVariable("type") String type,
                           @RequestBody(required = false) Object data) {

    return opsmxDashboardService.updateDashboardResponse(version, type, data)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.PUT)
  Object updateDashboardResponse1(@PathVariable("version") String version,
                                @PathVariable("type") String type,
                                @PathVariable("source") String source,
                                @RequestBody(required = false) Object data) {

    return opsmxDashboardService.updateDashboardResponse1(version, type, source, data)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.PUT)
  Object updateDashboardResponse2(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @RequestBody(required = false) Object data) {

    return opsmxDashboardService.updateDashboardResponse2(version, type, source, source1, data)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.PUT)
  Object updateDashboardResponse3(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @RequestBody(required = false) Object data) {

    return opsmxDashboardService.updateDashboardResponse3(version, type, source, source1, source2, data)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.PUT)
  Object updateDashboardResponse4(@PathVariable("version") String version,
                                  @PathVariable("type") String type,
                                  @PathVariable("source") String source,
                                  @PathVariable("source1") String source1,
                                  @PathVariable("source2") String source2,
                                  @PathVariable("source3") String source3,
                                  @RequestBody(required = false) Object data) {

    return opsmxDashboardService.updateDashboardResponse4(version, type, source, source1, source2, source3, data)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.PUT)
  Object updateDashboardResponse5(@PathVariable("version") String version,
                                  @PathVariable("type") String type,
                                  @PathVariable("source") String source,
                                  @PathVariable("source1") String source1,
                                  @PathVariable("source2") String source2,
                                  @PathVariable("source3") String source3,
                                  @PathVariable("source4") String source4,
                                  @RequestBody(required = false) Object data) {

    return opsmxDashboardService.updateDashboardResponse5(version, type, source, source1, source2, source3, source4, data)
  }

  @ApiOperation(value = "Endpoint for dashboard rest services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}", method = RequestMethod.PUT)
  Object updateDashboardResponse6(@PathVariable("version") String version,
                                  @PathVariable("type") String type,
                                  @PathVariable("source") String source,
                                  @PathVariable("source1") String source1,
                                  @PathVariable("source2") String source2,
                                  @PathVariable("source3") String source3,
                                  @PathVariable("source4") String source4,
                                  @PathVariable("source5") String source5,
                                  @RequestBody(required = false) Object data) {

    return opsmxDashboardService.updateDashboardResponse6(version, type, source, source1, source2, source3, source4, source5, data)
  }
}
