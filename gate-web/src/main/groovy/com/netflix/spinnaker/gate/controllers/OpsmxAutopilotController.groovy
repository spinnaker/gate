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

package com.netflix.spinnaker.gate.controllers

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.netflix.spinnaker.gate.config.ServiceConfiguration
import com.netflix.spinnaker.gate.model.ApprovalGateTriggerResponseModel
import com.netflix.spinnaker.gate.model.RegisterCanaryResponseModel
import com.netflix.spinnaker.gate.services.internal.OpsmxAutopilotService
import com.netflix.spinnaker.gate.services.internal.OpsmxOesService
import com.netflix.spinnaker.security.AuthenticatedRequest
import com.opsmx.spinnaker.gate.rbac.ApplicationFeatureRbac
import groovy.util.logging.Slf4j
import io.swagger.annotations.ApiOperation
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import retrofit.client.Header
import retrofit.client.Response

import javax.servlet.http.HttpServletRequest
import java.util.stream.Collectors

@RequestMapping("/autopilot")
@RestController
@Slf4j
@ConditionalOnExpression('${services.autopilot.enabled:false}')
class OpsmxAutopilotController {
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

  @Autowired
  OpsmxAutopilotService opsmxAutopilotService

  @Autowired(required = false)
  ApplicationFeatureRbac applicationFeatureRbac

  Gson gson = new Gson()

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}", method = RequestMethod.GET)
  Object getAutoResponse1(@PathVariable("type") String type) {
    return opsmxAutopilotService.getAutoResponse1(type)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @GetMapping(value = "/canaries/debugLogsData", produces = "application/zip")
  @ResponseBody Object downloadDebugData(@RequestParam(value = "id", required = false) Integer canaryId){
    Response response = opsmxAutopilotService.downloadDebugData(canaryId)
    InputStream inputStream = response.getBody().in()
    try {
      byte [] zipFile = IOUtils.toByteArray(inputStream)
      HttpHeaders headers = new HttpHeaders()
      headers.add("Content-Disposition", response.getHeaders().stream().filter({ header -> header.getName().trim().equalsIgnoreCase("Content-Disposition") }).collect(Collectors.toList()).get(0).value)
      return ResponseEntity.ok().headers(headers).body(zipFile)

    } finally{
      if (inputStream!=null){
        inputStream.close()
      }
    }

  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @GetMapping(value = "/canaries/clusterCompleteLog", produces = "application/zip")
  @ResponseBody Object downloadClusterInfo(@RequestParam(value = "canaryId", required = false) Integer canaryId,
                                           @RequestParam(value = "serviceId", required = false) Integer serviceId,
                                           @RequestParam(value = "clusterId", required = false) Integer clusterId,
                                           @RequestParam(value = "version", required = false) String version,
                                           @RequestParam(value = "testCaseId", required = false) Integer testCaseId){

    Response response = opsmxAutopilotService.downloadClusterInfo(canaryId, serviceId, clusterId, version, testCaseId)
    InputStream inputStream = response.getBody().in()
    try {
      byte [] zipFile = IOUtils.toByteArray(inputStream)
      HttpHeaders headers = new HttpHeaders()
      headers.add("Content-Disposition", response.getHeaders().stream().filter({ header -> header.getName().trim().equalsIgnoreCase("Content-Disposition") }).collect(Collectors.toList()).get(0).value)
      return ResponseEntity.ok().headers(headers).body(zipFile)

    } finally{
      if (inputStream!=null){
        inputStream.close()
      }
    }

  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @GetMapping(value = "/canaries/patternCompleteLog", produces = "application/zip")
  @ResponseBody Object downloadPatternCompleteLog(@RequestParam(value = "canaryId", required = false) Integer canaryId,
                                                  @RequestParam(value = "serviceId", required = false) Integer serviceId,
                                                  @RequestParam(value = "patternId", required = false) String patternId){

    Response response = opsmxAutopilotService.downloadPatternCompleteLog(canaryId, serviceId, patternId)
    InputStream inputStream = response.getBody().in()
    try {
      byte [] zipFile = IOUtils.toByteArray(inputStream)
      HttpHeaders headers = new HttpHeaders()
      headers.add("Content-Disposition", response.getHeaders().stream().filter({ header -> header.getName().trim().equalsIgnoreCase("Content-Disposition") }).collect(Collectors.toList()).get(0).value)
      return ResponseEntity.ok().headers(headers).body(zipFile)

    } finally{
      if (inputStream!=null){
        inputStream.close()
      }
    }

  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/api/{version}/registerCanary", method = RequestMethod.POST)
  Object triggerRegisterCanary(@PathVariable("version") String version, @RequestBody(required = false) Object data,
                               @RequestHeader(value = "x-spinnaker-user",required = false) String xSpinnakerUser, HttpServletRequest request) throws Exception {

    if (applicationFeatureRbac!=null){
      applicationFeatureRbac.authorizeUserForVerificationAndTestVerificationGateTrigger(request, data)
    }

    Response response = opsmxAutopilotService.triggerRegisterCanary(version, data,xSpinnakerUser)
    InputStream inputStream = null

    try {
      HttpHeaders headers = new HttpHeaders()
      List<Header> locationHeaders = response.getHeaders().stream().filter({ header -> header.getName().trim().equalsIgnoreCase("Location") }).collect(Collectors.toList())
      if (locationHeaders!=null && !locationHeaders.isEmpty()){
        headers.add("Location", locationHeaders.get(0).value)
      }

      inputStream = response.getBody().in()
      String responseBody = new String(IOUtils.toByteArray(inputStream))
      try {
        RegisterCanaryResponseModel registerCanaryResponseModel = gson.fromJson(responseBody, RegisterCanaryResponseModel.class)
        return new ResponseEntity(registerCanaryResponseModel, headers, HttpStatus.valueOf(response.getStatus()))
      }catch(JsonSyntaxException jse){
        log.error("JSON parsing failed and hence passing the String value : {}", jse.getMessage())
      }

      return new ResponseEntity(responseBody, headers, HttpStatus.valueOf(response.getStatus()))

    } finally{
      if (inputStream!=null){
        inputStream.close()
      }
    }
  }


  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}", method = RequestMethod.GET)
  Object getAutoResponse(@PathVariable("type") String type, @PathVariable("source") String source,
                         @RequestParam(value = "application", required = false) Integer id,
                         @RequestParam(value = "applicationId", required = false) Integer applicationId,
                         @RequestParam(value = "serviceId", required = false) Integer serviceId,
                         @RequestParam(value = "startTime", required = false) Long startTime,
                         @RequestParam(value = "endTime", required = false) Long endTime,
                         @RequestParam(value = "intervalMins", required = false) Float intervalMins,
                         @RequestParam(value = "limit", required = false) Integer limit,
                         @RequestParam(value = "sourceType", required = false) String sourceType,
                         @RequestParam(value = "datasourceType", required = false) String datasourceType,
                         @RequestParam(value = "accountName", required = false)  String accountName,
                         @RequestParam(name = "templateType",required = false) String templateType,
                         @RequestParam(value = "name", required = false) String name,
                         @RequestParam(name = "appId",required = false) Integer appId,
                         @RequestParam(value = "pipelineid", required = false) String pipelineid,
                         @RequestParam(value = "applicationName",required = false) String applicationName,
                         @RequestParam(value = "username", required = false) String username,
                         @RequestParam(value = "userName", required = false) String userName,
                         @RequestParam(value = "templateName", required = false) String templateName,
                         @RequestParam(value = "credentialType", required = false) String credentialType,
                         @RequestParam(value = "id", required = false) Integer canaryId,
                         @RequestParam(value = "service", required = false) Integer service,
                         @RequestParam(value = "canaryId", required = false) Integer canary,
                         @RequestParam(value = "canaryid", required = false) Integer canaryid,
                         @RequestParam(value = "clusterId", required = false) Long clusterId,
                         @RequestParam(value = "version", required = false) String  version,
                         @RequestParam(value="canaryAnalysisId", required=false) Integer canaryAnalysisId,
                         @RequestParam(value = "metric", required = false) String metric,
                         @RequestParam(value = "account", required = false) String account,
                         @RequestParam(value = "metricType", required = false) String metricType,
                         @RequestParam(value = "isBoxplotData", required = false) Boolean isBoxplotData,
                         @RequestParam(value = "metricname", required = false) String metricname,
                         @RequestParam(value = "numofver", required = false) Integer numofver,
                         @RequestParam(value = "serviceName", required = false) String serviceName,
                         @RequestParam(value = "platform", required = false) String platform,
                         @RequestParam(value = "ruleId", required = false) Integer ruleId,
                         @RequestParam(value = "zone", required = false) String zone,
                         @RequestParam(value = "type", required = false) String appType,
                         @RequestParam(value = "metricTemplate", required = false) String metricTemplate,
                         @RequestParam(value = "logTemplate", required = false) String logTemplate,
                         @RequestParam(value = "riskanalysis_id", required = false) Integer riskanalysis_id,
                         @RequestParam(value = "service_id", required = false) Integer service_id,
                         @RequestParam(value = "userId", required = false) Integer userId,
                         @RequestParam(value = "logTemplateName", required = false) String logTemplateName,
                         @RequestParam(value = "forceDelete", required = false) Boolean forceDelete,
                         @RequestParam(value = "deleteAssociateRuns", required = false) Boolean deleteAssociateRuns,
                         @RequestParam(value = "event", required = false) String event,
                         @RequestParam(value = "serviceList", required = false) List<String>  serviceList,
                         @RequestParam(value = "pipelineId", required = false) String pipelineId,
                         @RequestParam(value = "referer", required = false) String referer,
                         @RequestParam(value = "testCaseId", required = false) Integer testCaseId,
                         @RequestParam(value = "verificationType", required = false) String verificationType,
                         @RequestParam(value = "patternId", required = false) String patternId){

    return opsmxAutopilotService.getAutoResponse(type, source, id, applicationId, serviceId, startTime, endTime, intervalMins, limit, sourceType, datasourceType,
      accountName, templateType, name, appId, pipelineid, applicationName, username, userName, templateName, credentialType, canaryId, service, canary, canaryid, clusterId, version, canaryAnalysisId,
      metric,account,metricType,isBoxplotData,metricname,numofver,serviceName,platform,ruleId,zone,appType,metricTemplate,logTemplate,riskanalysis_id,service_id,
      userId,logTemplateName,forceDelete,deleteAssociateRuns, event, serviceList, pipelineId, referer, testCaseId, verificationType, patternId)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}", method = RequestMethod.GET)
  Object getAutoResponse4(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @RequestParam(value = "Ids", required = false) String[] applicationsIds,
                          @RequestParam(value = "datasourceType", required = false) String datasourceType,
                          @RequestParam(value = "canaryId", required = false) Integer canaryId,
                          @RequestParam(value = "serviceId", required = false) Integer serviceId,
                          @RequestParam(value = "pageNo", required = false) Integer pageNo,
                          @RequestParam(value = "pageLimit", required = false) Integer pageLimit,
                          @RequestParam(value = "sortBy", required = false) String sortBy,
                          @RequestParam(value = "sortOrder", required = false) String sortOrder,
                          @RequestParam(value = "applicationId", required = false) Integer applicationId,
                          @RequestParam(value = "noOfDays", required = false) Integer noOfDays,
                          @RequestParam(value = "patternId", required = false) String patternId,
                          @RequestParam(value = "search", required = false) String search,
                          @RequestParam(value = "verificationType", required = false) String verificationType) {

    return opsmxAutopilotService.getAutoResponse4(type, source, source1, applicationsIds, datasourceType, canaryId, serviceId, pageNo, pageLimit, sortBy, sortOrder, applicationId, noOfDays, patternId, search,verificationType)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}", method = RequestMethod.GET)
  Object getAutoResponse5(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                          @RequestParam(value = "canaryId", required = false) Integer canaryId,
                          @RequestParam(value = "verificationType", required = false) String verificationType,
                          @RequestParam(value = "permissionId", required = false) String permissionId) {
    return opsmxAutopilotService.getAutoResponse5(type, source, source1, source2, canaryId, verificationType, permissionId)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.GET)
  Object getAutoResponse6(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                          @PathVariable("source3") String source3,
                          @RequestParam(value = "pageNo", required = false) Integer pageNo,
                          @RequestParam(value = "pageLimit", required = false) Integer pageLimit,
                          @RequestParam(value = "noOfDays", required = false) Integer noOfDays,
                          @RequestParam(value = "search", required = false) String search,
                          @RequestParam(value = "verificationType", required = false) String verificationType,
                          @RequestParam(value = "status", required = false) String status) {

    return opsmxAutopilotService.getAutoResponse6(type, source, source1, source2, source3,pageNo,pageLimit,noOfDays,search,verificationType,status)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.GET)
  Object getAutoResponse7(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                          @PathVariable("source3") String source3,
                          @PathVariable("source4") String source4,
                          @RequestParam(value = "time", required = false) String time) {

    return opsmxAutopilotService.getAutoResponse7(type, source, source1, source2, source3, source4, time)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}", method = RequestMethod.GET)
  Object getAutoResponse8(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                          @PathVariable("source3") String source3,
                          @PathVariable("source4") String source4,
                          @PathVariable("source5") String source5,
                          @RequestParam(value = "imageId", required = false) String imageId,
                          @RequestParam(value = "canaryIds", required = false) String canaryIds,
                          @RequestParam(value = "gateIds", required = false) String gateIds) {

    return opsmxAutopilotService.getAutoResponse8(type, source, source1, source2, source3, source4, source5, imageId, canaryIds, gateIds)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}/{source6}/{source7}/{source8}", method = RequestMethod.GET)
  Object getAutoResponse9(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                          @PathVariable("source3") String source3,
                          @PathVariable("source4") String source4,
                          @PathVariable("source5") String source5,
                          @PathVariable("source6") String source6,
                          @PathVariable("source7") String source7,
                          @PathVariable("source8") String source8,
                          @RequestParam(value = "imageId", required = false) String imageId) {
    return opsmxAutopilotService.getAutoResponse9(type, source, source1, source2, source3, source4, source5, source6, source7, source8, imageId)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}/{source6}", method = RequestMethod.GET)
  Object getAutoResponse10(@PathVariable("type") String type,
                           @PathVariable("source") String source,
                           @PathVariable("source1") String source1,
                           @PathVariable("source2") String source2,
                           @PathVariable("source3") String source3,
                           @PathVariable("source4") String source4,
                           @PathVariable("source5") String source5,
                           @PathVariable("source6") String source6,
                           @RequestParam(value = "imageId", required = false) String imageId,
                           @RequestParam(value = "canaryIds", required = false) String canaryIds,
                           @RequestParam(value = "gateIds", required = false) String gateIds) {

    return opsmxAutopilotService.getAutoResponse10(type, source, source1, source2, source3, source4, source5, source6,imageId, canaryIds, gateIds)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}", method = RequestMethod.DELETE)
  Object deleteAutoResponse1(@PathVariable("type") String type) {

    return opsmxAutopilotService.deleteAutoResponse1(type)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}", method = RequestMethod.DELETE)
  Object deleteAutoResponse(@PathVariable("type") String type,
                           @PathVariable("source") String source,
                           @RequestParam(value = "applicationId", required = false) Integer applicationId,
                           @RequestParam(value = "pipelineid",required = false) String pipelineId,
                           @RequestParam(value = "applicationName",required = false) String applicationName,
                           @RequestParam(value = "accountName",required = false) String accountName,
                           @RequestParam(value = "sourceType",required = false) String sourceType,
                           @RequestParam(value = "credentialType",required = false) String credentialType,
                           @RequestParam(value = "canaryId",required = false) Integer canaryId,
                           @RequestParam(value = "forceDelete",required = false) Boolean forceDelete,
                           @RequestParam(value = "deleteAssociateRuns",required = false) Boolean deleteAssociateRuns,
                           @RequestParam(value = "templateName",required = false) String templateName) {
    return opsmxAutopilotService.deleteAutoResponse(type, source, applicationId, pipelineId, applicationName, accountName, sourceType, credentialType, canaryId,
      forceDelete, deleteAssociateRuns, templateName)

  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}", method = RequestMethod.DELETE)
  Object deleteAutoResponse3(@PathVariable("type") String type,
                            @PathVariable("source") String source,
                            @PathVariable("source1") String source1) {

    return opsmxAutopilotService.deleteAutoResponse3(type, source, source1)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}", method = RequestMethod.DELETE)
  Object deleteAutoResponse4(@PathVariable("type") String type,
                             @PathVariable("source") String source,
                             @PathVariable("source1") String source1,
                             @PathVariable("source2")String source2) {

    return opsmxAutopilotService.deleteAutoResponse4(type, source, source1, source2)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.DELETE)
  Object deleteAutoResponse5(@PathVariable("type") String type,
                             @PathVariable("source") String source,
                             @PathVariable("source1") String source1,
                             @PathVariable("source2") String source2,
                             @PathVariable("source3") String source3) {

    return opsmxAutopilotService.deleteAutoResponse5(type, source, source1, source2, source3)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.DELETE)
  Object deleteAutoResponse6(@PathVariable("type") String type,
                             @PathVariable("source") String source,
                             @PathVariable("source1") String source1,
                             @PathVariable("source2") String source2,
                             @PathVariable("source3") String source3,
                             @PathVariable("source4") String source4) {

    return opsmxAutopilotService.deleteAutoResponse6(type, source, source1, source2, source3, source4)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}", method = RequestMethod.POST)
  Object postAutoResponse(@PathVariable("type") String type,
                         @RequestBody(required = false) Object data) {

    return opsmxAutopilotService.postAutoResponse(type,data)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}", method = RequestMethod.POST)
  Object postAutoResponse1(@PathVariable("type") String type,
                           @PathVariable("source") String source,
                           @RequestParam(value = "isEdit", required = false) Boolean isEdit,
                           @RequestParam(value = "userName", required = false) String userName,
                           @RequestParam(value = "userId", required = false) Integer userId,
                           @RequestParam(value = "canaryId", required = false) Integer canaryId,
                           @RequestParam(value = "logTemplateName", required = false) String logTemplateName,
                           @RequestParam(value = "serviceId", required = false) Integer serviceId,
                           @RequestParam(value = "testCaseId", required = false) Integer testCaseId,
                           @RequestBody(required = false) Object data) {

    return opsmxAutopilotService.postAutoResponse1(type, source, isEdit, userName, userId, canaryId, logTemplateName, serviceId, testCaseId, data)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}", method = RequestMethod.POST)
  Object postAutoResponse4(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @RequestBody(required = false) Object data) {

    return opsmxAutopilotService.postAutoResponse4(type, source, source1, data)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}", method = RequestMethod.POST)
  Object postAutoResponse5(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                           @RequestParam(value = "isEdit", required = false) Boolean isEdit,
                          @RequestBody(required = false) Object data) {

    return opsmxAutopilotService.postAutoResponse5(type, source, source1, source2, isEdit, data)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.POST)
  Object postAutoResponse6(@PathVariable("type") String type,
                          @PathVariable("source") String source,
                          @PathVariable("source1") String source1,
                          @PathVariable("source2") String source2,
                          @PathVariable("source3") String source3,
                           @RequestParam(value = "isEdit", required = false) Boolean isEdit,
                          @RequestBody(required = false) Object data) {

    return opsmxAutopilotService.postAutoResponse6(type, source, source1, source2, source3, isEdit, data)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}", method = RequestMethod.PUT)
  Object updateAutopilotResponse(@PathVariable("type") String type,
                                @RequestBody(required = false) Object data) {

    return opsmxAutopilotService.updateAutopilotResponse(type, data)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}", method = RequestMethod.PUT)
  Object updateAutopilotResponse1(@PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @RequestBody(required = false) Object data) {

    return opsmxAutopilotService.updateAutopilotResponse1(type, source, data)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}", method = RequestMethod.PUT)
  Object updateAutopilotResponse2(@PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @RequestBody(required = false) Object data) {

    return opsmxAutopilotService.updateAutopilotResponse2(type, source, source1, data)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}", method = RequestMethod.PUT)
  Object updatePlatformResponse3(@PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @RequestBody(required = false) Object data) {

    return opsmxAutopilotService.updateAutopilotResponse3(type, source, source1, source2, data)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.PUT)
  Object updatePlatformResponse3(@PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @PathVariable("source3") String source3,
                                 @RequestBody(required = false) Object data) {

    return opsmxAutopilotService.updateAutopilotResponse4(type, source, source1, source2, source3, data)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.PUT)
  Object updatePlatformResponse3(@PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @PathVariable("source3") String source3,
                                 @PathVariable("source4") String source4,
                                 @RequestBody(required = false) Object data) {

    return opsmxAutopilotService.updateAutopilotResponse5(type, source, source1, source2, source3, source4, data)
  }

  @ApiOperation(value = "Endpoint for autopilot rest services")
  @RequestMapping(value = "/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}", method = RequestMethod.PUT)
  Object updatePlatformResponse4(@PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @PathVariable("source1") String source1,
                                 @PathVariable("source2") String source2,
                                 @PathVariable("source3") String source3,
                                 @PathVariable("source4") String source4,
                                 @PathVariable("source5") String source5,
                                 @RequestBody(required = false) Object data) {

    return opsmxAutopilotService.updateAutopilotResponse6(type, source, source1, source2, source3, source4, source5, data)
  }

}
