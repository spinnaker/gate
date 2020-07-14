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

import com.netflix.spinnaker.gate.services.internal.GoogleCloudBuildTrigger
import com.netflix.spinnaker.gate.services.internal.IgorService
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.util.UriUtils
import retrofit.RetrofitError

@CompileStatic
@Component
class BuildService {

  @Autowired(required = false)
  IgorService igorService

  private String encode(uri) {
    return UriUtils.encodeFragment(uri.toString(), "UTF-8")
  }

  List<String> getBuildMasters(String buildServiceType) {
    if (!igorService) {
      return []
    }
    if (buildServiceType) {
      return igorService.getBuildMasters(buildServiceType)
    } else {
      return igorService.getBuildMasters()
    }
  }

  List<String> getBuildMasters() {
    if (!igorService) {
      return []
    }
    return igorService.getBuildMasters()
  }


  List<String> getJobsForBuildMaster(String buildMaster) {
    if (!igorService) {
      return []
    }
    try {
      igorService.getJobsForBuildMaster(buildMaster)
    } catch (RetrofitError e) {
      if (e.response?.status == 404) {
        throw new BuildMasterNotFound("Build master '${buildMaster}' not found")
      }
      throw e
    }
  }

  List<GoogleCloudBuildTrigger> getGoogleCloudBuildTriggersForAccount(String account) {
    if (!igorService) {
      return []
    }
    try {
      igorService.getGoogleCloudBuildTriggers(account)
    } catch (RetrofitError e) {
      if (e.response?.status == 404) {
        throw new GCBAccountNotFound("Account '${account}' not found")
      }

      throw e
    }
  }

  Map getJobConfig(String buildMaster, String job) {
    if (!igorService) {
      return [:]
    }
    try {
      igorService.getJobConfig(buildMaster, encode(job))
    } catch (RetrofitError e) {
      if (e.response?.status == 404) {
        throw new BuildMasterNotFound("Build master '${buildMaster}' not found")
      }

      throw e
    }
  }

  List getBuilds(String buildMaster, String job) {
    if (!igorService) {
      return []
    }
    try {
      igorService.getBuilds(buildMaster, encode(job))
    } catch (RetrofitError e) {
      if (e.response?.status == 404) {
        throw new BuildMasterNotFound("Build master '${buildMaster}' not found")
      }

      throw e
    }
  }

  Map getBuild(String buildMaster, String job, String number) {
    if (!igorService) {
      return [:]
    }
    try {
      igorService.getBuild(buildMaster, encode(job), number)
    } catch (RetrofitError e) {
      if (e.response?.status == 404) {
        throw new BuildMasterNotFound("Build master '${buildMaster}' not found")
      }

      throw e
    }
  }

}
