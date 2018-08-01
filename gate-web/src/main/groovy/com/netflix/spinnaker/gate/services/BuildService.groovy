/*
 * Copyright 2014 Netflix, Inc.
 * Copyright (c) 2017, 2018, Oracle Corporation and/or its affiliates. All rights reserved.
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


package com.netflix.spinnaker.gate.services

import com.netflix.hystrix.exception.HystrixBadRequestException
import com.netflix.spinnaker.gate.services.commands.HystrixFactory
import com.netflix.spinnaker.gate.services.internal.IgorService
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.util.UriUtils
import retrofit.RetrofitError

@CompileStatic
@Component
class BuildService {
  private static final String GROUP = "builds"

  @Autowired(required = false)
  IgorService igorService

  private String encode(uri) {
    return UriUtils.encodeFragment(uri.toString(), "UTF-8")
  }

  List<String> getBuildMasters(String buildServiceType) {
    if (!igorService) {
      return []
    }
    HystrixFactory.newListCommand(GROUP, "masters") { 
      if(buildServiceType) {
        igorService.getBuildMasters(buildServiceType)
      } else {
        igorService.getBuildMasters()
      }
    } execute()
  }

  List<String> getBuildMasters() {
    if (!igorService) {
      return []
    }
    HystrixFactory.newListCommand(GROUP, "masters") {
      igorService.getBuildMasters()
    } execute()
  }


  List<String> getJobsForBuildMaster(String buildMaster) {
    if (!igorService) {
      return []
    }
    HystrixFactory.newListCommand(GROUP, "jobsForBuildMaster") {
      try {
        igorService.getJobsForBuildMaster(buildMaster)
      } catch (RetrofitError e) {
        if (e.response?.status == 404) {
          throw new BuildMasterNotFound("Build master '${buildMaster}' not found")
        }

        throw e
      }
    } execute()
  }

  Map getJobConfig(String buildMaster, String job) {
    if (!igorService) {
      return [:]
    }
    HystrixFactory.newMapCommand(GROUP, "jobConfig") {
      try {
        igorService.getJobConfig(buildMaster, encode(job))
      } catch (RetrofitError e) {
        if (e.response?.status == 404) {
          throw new BuildMasterNotFound("Build master '${buildMaster}' not found")
        }

        throw e
      }
    } execute()
  }

  List getBuilds(String buildMaster, String job) {
    if (!igorService) {
      return []
    }
    HystrixFactory.newListCommand(GROUP, "buildsForJob") {
      try {
        igorService.getBuilds(buildMaster, encode(job))
      } catch (RetrofitError e) {
        if (e.response?.status == 404) {
          throw new BuildMasterNotFound("Build master '${buildMaster}' not found")
        }

        throw e
      }
    } execute()
  }

  Map getBuild(String buildMaster, String job, String number) {
    if (!igorService) {
      return [:]
    }
    HystrixFactory.newMapCommand(GROUP, "buildDetails") {
      try {
        igorService.getBuild(buildMaster, encode(job), number)
      } catch (RetrofitError e) {
        if (e.response?.status == 404) {
          throw new BuildMasterNotFound("Build master '${buildMaster}' not found")
        }

        throw e
      }
    } execute()
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @InheritConstructors
  static class BuildMasterNotFound extends HystrixBadRequestException {}
}
