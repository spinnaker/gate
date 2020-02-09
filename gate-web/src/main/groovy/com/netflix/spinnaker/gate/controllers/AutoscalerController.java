/*
 * Copyright 2020 Google, Inc.
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

import com.netflix.spinnaker.gate.services.AutoscalerService;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/applications/{application}/autoscalers")
public class AutoscalerController {
  private final AutoscalerService autoscalerService;

  @Autowired
  AutoscalerController(AutoscalerService autoscalerService) {
    this.autoscalerService = autoscalerService;
  }

  @ApiOperation(value = "Retrieve a list of autoscalers for an application", response = List.class)
  @RequestMapping(method = RequestMethod.GET)
  public List<Map> getAutoscalersForApplication(@PathVariable String application) {
    return this.autoscalerService.getAutoscalersForApplication(application);
  }
}
