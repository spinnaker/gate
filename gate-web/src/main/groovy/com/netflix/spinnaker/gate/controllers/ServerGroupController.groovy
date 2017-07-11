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


package com.netflix.spinnaker.gate.controllers

import com.netflix.spinnaker.gate.services.ServerGroupService
import com.netflix.spinnaker.kork.web.exceptions.NotFoundException
import groovy.transform.CompileStatic
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@CompileStatic
@RestController
class ServerGroupController {
  @Autowired
  ServerGroupService serverGroupService

  @ApiOperation(value = "Retrieve a list of server groups for a given application")
  @RequestMapping(value = "/applications/{applicationName}/serverGroups", method = RequestMethod.GET)
  List getServerGroups(@PathVariable String applicationName,
                       @RequestParam(required = false, value = 'expand', defaultValue = 'false') String expand,
                       @RequestParam(required = false, value = 'cloudProvider') String cloudProvider,
                       @RequestParam(required = false, value = 'clusters') String clusters,
                       @RequestHeader(value = "X-RateLimit-App", required = false) String sourceApp) {
    serverGroupService.getForApplication(applicationName, expand, cloudProvider, clusters, sourceApp)
  }

  @ApiOperation(value = "Retrieve a server group's details")
  @RequestMapping(value = "/applications/{applicationName}/serverGroups/{account}/{region}/{serverGroupName:.+}", method = RequestMethod.GET)
  Map getServerGroupDetails(@PathVariable String applicationName,
                            @PathVariable String account,
                            @PathVariable String region,
                            @PathVariable String serverGroupName,
                            @RequestHeader(value = "X-RateLimit-App", required = false) String sourceApp) {
    def serverGroupDetails = serverGroupService.getForApplicationAndAccountAndRegion(applicationName, account, region, serverGroupName, sourceApp)
    if (!serverGroupDetails) {
      throw new NotFoundException("Server group now found (id: ${serverGroupName})")
    }

    return serverGroupDetails
  }
}
