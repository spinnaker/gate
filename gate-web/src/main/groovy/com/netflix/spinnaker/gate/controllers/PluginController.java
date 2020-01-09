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

package com.netflix.spinnaker.gate.controllers;

import com.netflix.spinnaker.gate.services.TaskService;
import com.netflix.spinnaker.gate.services.internal.Front50Service;
import com.netflix.spinnaker.kork.web.exceptions.InvalidRequestException;
import com.netflix.spinnaker.security.AuthenticatedRequest;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/pluginArtifacts")
public class PluginController {

  // TODO: configurable.
  private static final String DEFAULT_APPLICATION = "spinnaker";

  private TaskService taskService;
  private Front50Service front50Service;

  @Autowired
  public PluginController(TaskService taskService, Front50Service front50Service) {
    this.taskService = taskService;
    this.front50Service = front50Service;
  }

  @ApiOperation(value = "Persist plugin metadata")
  @RequestMapping(
      method = {RequestMethod.POST, RequestMethod.PUT},
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(value = HttpStatus.OK)
  void persistPluginMetadata(@RequestBody Map pluginMetaData) {
    List<Map<String, Object>> jobs = new ArrayList<>();
    Map<String, Object> job = new HashMap<>();
    job.put("type", "upsertPluginArtifact");
    job.put("pluginArtifact", pluginMetaData);
    job.put("user", AuthenticatedRequest.getSpinnakerUser().orElse("anonymous"));
    jobs.add(job);

    initiateTask("Create/Update plugin", jobs);
  }

  @ApiOperation(value = "Delete plugin metadata with the provided Id")
  @RequestMapping(
      value = "/{Id}",
      method = {RequestMethod.DELETE},
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(value = HttpStatus.NO_CONTENT)
  void deletePluginMetadata(@PathVariable String Id) {
    List<Map<String, Object>> jobs = new ArrayList<>();
    Map<String, Object> job = new HashMap<>();
    job.put("type", "deletePluginArtifact");
    job.put("pluginArtifactId", Id);
    job.put("user", AuthenticatedRequest.getSpinnakerUser().orElse("anonymous"));
    jobs.add(job);

    initiateTask("Delete Plugin metadata", jobs);
  }

  @ApiOperation(value = "Get all plugin metadata")
  @RequestMapping(method = RequestMethod.GET)
  List<Map> getAllPluginMetadata(
      @RequestParam(value = "service", required = false) String service) {
    return front50Service.getPluginArtifacts(service);
  }

  private void initiateTask(String description, List<Map<String, Object>> jobs) {
    Map<String, Object> operation = new HashMap<>();
    operation.put("description", description);
    operation.put("application", DEFAULT_APPLICATION);
    operation.put("job", jobs);
    Map result = taskService.createAndWaitForCompletion(operation);
    String resultStatus = (String) result.get("status");

    if (!"SUCCEEDED".equalsIgnoreCase(resultStatus)) {
      throw new InvalidRequestException(
          "Operation: '" + description + "' failed with Status: " + resultStatus);
    }
  }
}
