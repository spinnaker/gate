/*
 * Copyright 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import com.netflix.spinnaker.gate.services.JobService
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@CompileStatic
@RestController
class JobController {
  @Autowired
  JobService jobService

  @RequestMapping(value = "/applications/{applicationName}/jobs", method = RequestMethod.GET)
  List getJobs(@PathVariable String applicationName, @RequestParam(required = false, value = 'expand', defaultValue = 'false') String expand) {
    jobService.getForApplication(applicationName, expand)
  }

  @RequestMapping(value = "/applications/{applicationName}/jobs/{account}/{region}/{name}", method = RequestMethod.GET)
  Map getJob(@PathVariable String applicationName, @PathVariable String account,
             @PathVariable String region,
             @PathVariable String name,
             @RequestParam(required = false, value = 'expand', defaultValue = 'false') String expand) {
    jobService.getForApplicationAndAccountAndRegion(applicationName, account, region, name)
  }
}
