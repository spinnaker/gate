/*
 * Copyright 2015 Google, Inc.
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

package com.netflix.spinnaker.gate.services

import com.fasterxml.jackson.annotation.JsonInclude
import com.netflix.spinnaker.gate.services.internal.RoscoServiceSelector
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Slf4j
@Component
@ConfigurationProperties('services.rosco.defaults')
class BakeService {
  @Autowired(required = false)
  RoscoServiceSelector roscoServiceSelector

  // Default bake options from configuration.
  List<BakeOptions> bakeOptions
  // If set, use bake options defined in gate.yml instead of calling rosco
  boolean useDefaultBakeOptions

  def bakeOptions() {
    (roscoServiceSelector && !useDefaultBakeOptions) ?
      roscoServiceSelector.withLocation().bakeOptions() : bakeOptions
  }

  def bakeOptions(String cloudProvider) {
    if (roscoServiceSelector) {
      return roscoServiceSelector.withLocation().bakeOptions(cloudProvider)
    }
    def bakeOpts = bakeOptions.find { it.cloudProvider == cloudProvider }
    if (bakeOpts) {
      return bakeOpts
    }
    throw new IllegalArgumentException("Bake options for cloud provider ${cloudProvider} not found")
  }

  String lookupLogs(String region, String statusId) {
    if (roscoServiceSelector) {
      def logsMap = roscoServiceSelector.withLocation(region).lookupLogs(region, statusId)

      if (logsMap?.logsContent) {
        return "<pre>$logsMap.logsContent</pre>"
      } else {
        throw new IllegalArgumentException("Bake logs not found.")
      }
    } else {
      throw new IllegalArgumentException("Bake logs retrieval not supported.")
    }
  }

  static class BakeOptions {
    String cloudProvider
    List<BaseImage> baseImages
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  static class BaseImage {
    String id
    String shortDescription
    String detailedDescription
    String displayName
    String packageType
    List<String> vmTypes
  }
}
