/*
 * Copyright 2017 Netflix, Inc.
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
package com.netflix.spinnaker.gate.controllers;

import com.netflix.spinnaker.gate.security.RequestContext;
import com.netflix.spinnaker.gate.services.internal.OrcaServiceSelector;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExecutionsController {

  private OrcaServiceSelector orcaServiceSelector;

  @Autowired
  public ExecutionsController(OrcaServiceSelector orcaServiceSelector) {
    this.orcaServiceSelector = orcaServiceSelector;
  }

  @ApiOperation(value = "Retrieve a list of the most recent pipeline executions for the provided `pipelineConfigIds` that match the provided `statuses` query parameter")
  @RequestMapping(value = "/executions", method = RequestMethod.GET)
  List getLatestExecutionsByConfigIds(@RequestParam(value = "pipelineConfigIds") String pipelineConfigIds,
                                      @RequestParam(value = "limit", required = false) Integer limit,
                                      @RequestParam(value = "statuses", required = false) String statuses) {
    return orcaServiceSelector.withContext(RequestContext.get()).getLatestExecutionsByConfigIds(pipelineConfigIds, limit, statuses);
  }

  @ApiOperation(value = "Search for pipeline executions using a combination of criteria")
  @RequestMapping(value = "/applications/{application}/executions/search", method = RequestMethod.GET)
  List searchForPipelineExecutions(
    @PathVariable(value = "application") String application,
    @RequestParam(value = "triggerTypes", required = false) String triggerTypes,
    @RequestParam(value = "pipelineName", required = false) String pipelineName,
    @RequestParam(value = "eventId", required = false) String eventId,
    @RequestParam(value = "trigger", required = false) String trigger,
    @RequestParam(value = "triggerTimeStartBoundary", defaultValue = "0") long triggerTimeStartBoundary,
    @RequestParam(value = "triggerTimeEndBoundary", defaultValue = "9223372036854775807" /* Long.MAX_VALUE */) long triggerTimeEndBoundary,
    @RequestParam(value = "statuses", required = false) String statuses,
    @RequestParam(value = "startIndex", defaultValue =  "0") int startIndex,
    @RequestParam(value = "size", defaultValue = "10") int size,
    @RequestParam(value = "reverse", defaultValue = "false") boolean reverse,
    @RequestParam(value = "expand", defaultValue = "false") boolean expand
  ) {
    return orcaServiceSelector.withContext(RequestContext.get()).searchForPipelineExecutions(application, triggerTypes, pipelineName, eventId, trigger, triggerTimeStartBoundary, triggerTimeEndBoundary, statuses, startIndex, size, reverse, expand);
  }
}
