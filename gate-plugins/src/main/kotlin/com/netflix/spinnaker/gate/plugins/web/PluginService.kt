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

package com.netflix.spinnaker.gate.plugins.web

import com.netflix.spinnaker.gate.services.TaskService
import com.netflix.spinnaker.security.AuthenticatedRequest
import org.springframework.stereotype.Service

@Service
class PluginService(
  private val taskService: TaskService,
  private val spinnakerExtensionsConfigProperties: SpinnakerExtensionsConfigProperties
) {

  fun upsertPluginInfo(pluginInfo: Map<String, Any>): Map<String, Any> {
    val jobs: MutableList<Map<String, Any>> = mutableListOf()
    val job: MutableMap<String, Any> = mutableMapOf()

    job["type"] = "upsertPluginInfo"
    job["pluginInfo"] = pluginInfo
    job["user"] = AuthenticatedRequest.getSpinnakerUser().orElse("anonymous")
    jobs.add(job)

    return initiateTask("Upsert plugin info with Id: " + pluginInfo["id"], jobs)
  }

  fun deletePluginInfo(id: String): Map<String, Any> {
    val jobs: MutableList<Map<String, Any>> = mutableListOf()
    val job: MutableMap<String, Any> = mutableMapOf()

    job["type"] = "deletePluginInfo"
    job["pluginInfoId"] = id
    job["user"] = AuthenticatedRequest.getSpinnakerUser().orElse("anonymous")
    jobs.add(job)

    return initiateTask("Delete Plugin info with Id: $id", jobs)
  }

  private fun initiateTask(description: String, jobs: List<Map<String, Any>>): Map<String, Any> {
    val operation: MutableMap<String?, Any?> = mutableMapOf()

    operation["description"] = description
    operation["application"] = spinnakerExtensionsConfigProperties.applicationName
    operation["job"] = jobs

    return taskService.create(operation) as Map<String, Any>
  }
}
