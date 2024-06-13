/*
 * Copyright 2017 Google, Inc.
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


import com.netflix.spinnaker.gate.services.internal.KayentaService
import com.netflix.spinnaker.kork.retrofit.exceptions.SpinnakerServerException
import com.netflix.spinnaker.kork.retrofit.exceptions.UpstreamBadRequest

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.stereotype.Component

@Component
@CompileStatic
@ConditionalOnExpression('${services.kayenta.enabled:false}')
class V2CanaryService {

  @Autowired
  KayentaService kayentaService

  List getCredentials() {
    try {
      return kayentaService.getCredentials()
    } catch (SpinnakerServerException error) {
      throw UpstreamBadRequest.classifyError(error)
    }
  }

  List listMetricsServiceMetadata(String filter, String metricsAccountName) {
    try {
      return kayentaService.listMetricsServiceMetadata(filter, metricsAccountName)
    } catch (SpinnakerServerException error) {
      throw UpstreamBadRequest.classifyError(error)
    }
  }

  List listJudges() {
    try {
      return kayentaService.listJudges()
    } catch (SpinnakerServerException error) {
      throw UpstreamBadRequest.classifyError(error)
    }
  }

  Map initiateCanaryWithConfig(Map adhocExecutionRequest,
                               String application,
                               String parentPipelineExecutionId,
                               String metricsAccountName,
                               String storageAccountName) {
    try {
      return kayentaService.initiateCanaryWithConfig(adhocExecutionRequest,
        application,
        parentPipelineExecutionId,
        metricsAccountName,
        storageAccountName)
    } catch (SpinnakerServerException error) {
      throw UpstreamBadRequest.classifyError(error)
    }
  }

  Map initiateCanary(String canaryConfigId,
                     Map executionRequest,
                     String application,
                     String parentPipelineExecutionId,
                     String metricsAccountName,
                     String storageAccountName,
                     String configurationAccountName) {
    try {
      return kayentaService.initiateCanary(canaryConfigId,
                                           executionRequest,
                                           application,
                                           parentPipelineExecutionId,
                                           metricsAccountName,
                                           storageAccountName,
                                           configurationAccountName)
    } catch (SpinnakerServerException error) {
      throw UpstreamBadRequest.classifyError(error)
    }
  }

  Map getCanaryResults(String canaryExecutionId, String storageAccountName) {
    try {
      return kayentaService.getCanaryResult(canaryExecutionId, storageAccountName)
    } catch (SpinnakerServerException error) {
      throw UpstreamBadRequest.classifyError(error)
    }
  }

  List getCanaryResultsByApplication(String application, int limit, int page, String statuses, String storageAccountName) {
    try {
      return kayentaService.getCanaryResultsByApplication(application, limit, page, statuses, storageAccountName)
    } catch (SpinnakerServerException error) {
      throw UpstreamBadRequest.classifyError(error)
    }
  }

  List getMetricSetPairList(String metricSetPairListId, String storageAccountName) {
    try {
      return kayentaService.getMetricSetPairList(metricSetPairListId, storageAccountName)
    } catch (SpinnakerServerException error) {
      throw UpstreamBadRequest.classifyError(error)
    }
  }
}
