/*
 * Copyright 2023 OpsMx, Inc.
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

import com.netflix.spinnaker.gate.services.internal.OpsmxSsdService
import groovy.util.logging.Slf4j
import io.swagger.annotations.ApiOperation
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import retrofit.client.Response

import java.util.stream.Collectors

@RequestMapping("/ssdservice")
@RestController
@Slf4j
@ConditionalOnExpression('${services.ssdservice.enabled:false}')
class OpsmxSsdController {

  @Autowired
  OpsmxSsdService opsMxSsdService

  @ApiOperation(value = "Endpoint for ssd rest services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.POST)
  Object postSsdServiceResponse(@PathVariable("version") String version,
                                @PathVariable("type") String type,
                                @RequestParam(value = "stage", required = false) String stage,
                                @RequestParam(value = "policy", required = false) String policy,
                                @RequestParam(value = "policyId", required = false) Integer policyId,
                                @RequestParam(value = "id", required = false) Integer id,
                                @RequestParam(value = "scope", required = false) String scope,
                                @RequestParam(value = "appId", required = false) String appId,
                                @RequestBody(required = false) Object data) {

    return opsMxSsdService.postSsdServiceResponse(version, type, stage, policy, policyId,id,scope,appId,data)
  }

  @ApiOperation(value = "Endpoint for ssd rest services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.POST)
  Object postSsdServiceResponse3(@PathVariable("version") String version,
                                 @PathVariable("type") String type,
                                 @PathVariable("source") String source,
                                 @RequestParam(value = "id", required = false) String id,
                                 @RequestBody(required = false) Object data) {
    return opsMxSsdService.postSsdServiceResponse3(version, type, source, id, data)
  }

  @ApiOperation(value = "Endpoint for ssd services")
  @RequestMapping(value = "/{version}/{type}", method = RequestMethod.GET)
  Object getSsdResponse(@PathVariable("version") String version,
                        @PathVariable("type") String type,
                        @RequestParam(value = "account", required = false) String account,
                        @RequestParam(value = "appId", required = false) String appId,
                        @RequestParam(value = "image", required = false) String image,
                        @RequestParam(value = "imageTag", required = false) String imageTag,
                        @RequestParam(value = "stage", required = false) String stage,
                        @RequestParam(value = "deployedAt", required = false) String deployedAt,
                        @RequestParam(value = "appName", required = false) String appName,
                        @RequestParam(value = "pageNo", required = false) Integer pageNo,
                        @RequestParam(value = "pageLimit", required = false) Integer pageLimit,
                        @RequestParam(value = "sortBy", required = false) String sortBy,
                        @RequestParam(value = "sortOrder", required = false) String sortOrder,
                        @RequestParam(value = "search", required = false) String search,
                        @RequestParam(value = "noOfDays", required = false) Integer noOfDays,
                        @RequestParam(value = "policy", required = false) String policy,
                        @RequestParam(value = "typeList", required = false) String typeList,
                        @RequestParam(value = "alertName", required = false) String alertName,
                        @RequestParam(value = "id", required = false) String id,
                        @RequestParam(value = "startTime", required = false) String startTime,
                        @RequestParam(value = "endTime", required = false) String endTime,
                        @RequestParam(value = "severity", required = false) String severity,
                        @RequestParam(value = "scope", required = false) String scope,
                        @RequestParam(value = "current", required = false) String current){
    return opsMxSsdService.getSddResponse1(version, type, account, appId, image, imageTag, stage, deployedAt, appName, pageNo, pageLimit, sortBy, sortOrder, search, noOfDays, policy,typeList, alertName, id, startTime, endTime, severity,scope, current)
  }

  @ApiOperation(value = "Endpoint for ssd services")
  @RequestMapping(value = "/{version}/{type}/{source}", method = RequestMethod.GET)
  Object getSsdResponse1(@PathVariable("version") String version,
                         @PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @RequestParam(value = "account", required = false) String account,
                         @RequestParam(value = "appId", required = false) String appId,
                         @RequestParam(value = "image", required = false) String image,
                         @RequestParam(value = "imageTag", required = false) String imageTag,
                         @RequestParam(value = "stage", required = false) String stage,
                         @RequestParam(value = "deployedAt", required = false) String deployedAt,
                         @RequestParam(value = "appName", required = false) String appName,
                         @RequestParam(value = "pageNo", required = false) Integer pageNo,
                         @RequestParam(value = "pageLimit", required = false) Integer pageLimit,
                         @RequestParam(value = "sortBy", required = false) String sortBy,
                         @RequestParam(value = "sortOrder", required = false) String sortOrder,
                         @RequestParam(value = "search", required = false) String search,
                         @RequestParam(value = "noOfDays", required = false) Integer noOfDays,
                         @RequestParam(value = "alertName", required = false) String alertName,
                         @RequestParam(value = "riskStatus", required = false) String riskStatus,
                         @RequestParam(value = "id", required = false) String id,
                         @RequestParam(value = "scope", required = false) String scope) {
    return opsMxSsdService.getSddResponse2(version, type, source, account, appId, image, imageTag, stage, deployedAt, appName, pageNo, pageLimit, sortBy, sortOrder, search, noOfDays, alertName, riskStatus, id,scope)
  }

  @ApiOperation(value = "Endpoint for ssd services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}", method = RequestMethod.GET)
  Object getSsdResponse2(@PathVariable("version") String version,
                         @PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @PathVariable("source1") String source1,
                         @RequestParam(value = "account", required = false) String account,
                         @RequestParam(value = "appId", required = false) Integer appId,
                         @RequestParam(value = "image", required = false) String image,
                         @RequestParam(value = "appName", required = false) String appName,
                         @RequestParam(value = "noOfDays", required = false) Integer noOfDays,
                         @RequestParam(value = "pageNo", required = false) Integer pageNo,
                         @RequestParam(value = "pageLimit", required = false) Integer pageLimit,
                         @RequestParam(value = "kind", required = false) String kind,
                         @RequestParam(value = "id", required = false) String id) {
    return opsMxSsdService.getSddResponse3(version, type, source, source1,account , appId, image, appName, noOfDays,pageNo,pageLimit,kind,id)
  }

  @ApiOperation(value = "Endpoint for ssd services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}", method = RequestMethod.GET)
  Object getSsdResponse3(@PathVariable("version") String version,
                         @PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @PathVariable("source1") String source1,
                         @PathVariable("source2") String source2,
                         @RequestParam(value = "account", required = false) String account,
                         @RequestParam(value = "appId", required = false) Integer appId,
                         @RequestParam(value = "image", required = false) String image,
                         @RequestParam(value = "imageTag", required = false) String imageTag,
                         @RequestParam(value = "stage", required = false) String stage,
                         @RequestParam(value = "deployedAt", required = false) String deployedAt,
                         @RequestParam(value = "appName", required = false) String appName,
                         @RequestParam(value = "pageNo", required = false) Integer pageNo,
                         @RequestParam(value = "pageLimit", required = false) Integer pageLimit,
                         @RequestParam(value = "sortBy", required = false) String sortBy,
                         @RequestParam(value = "sortOrder", required = false) String sortOrder,
                         @RequestParam(value = "search", required = false) String search,
                         @RequestParam(value = "noOfDays", required = false) Integer noOfDays,
                         @RequestParam(value = "id", required = false) String id,
                         @RequestParam(value = "fromImage", required = false) String fromImage,
                         @RequestParam(value = "toImage", required = false) String toImage,
                         @RequestParam(value = "fromImageTag", required = false) String fromImageTag,
                         @RequestParam(value = "toImageTag", required = false) String toImageTag,
                         @RequestParam(value = "fromAccount", required = false) String fromAccount,
                         @RequestParam(value = "toAccount", required = false) String toAccount,
                         @RequestParam(value = "kind", required = false) String kind,
                         @RequestParam(value = "active", required = false) String active) {
    return opsMxSsdService.getSddResponse4(version, type, source, source1, source2, account, appId, image, imageTag, stage, deployedAt, appName, pageNo, pageLimit, sortBy, sortOrder, search, noOfDays, id, fromImage, toImage, fromImageTag, toImageTag, fromAccount, toAccount,kind,active)
  }

  @ApiOperation(value = "Endpoint for ssd services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}", method = RequestMethod.GET)
  Object getSsdResponse4(@PathVariable("version") String version,
                         @PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @PathVariable("source1") String source1,
                         @PathVariable("source2") String source2,
                         @PathVariable("source3") String source3,
                         @RequestParam(value = "appId", required = false) Integer appId,
                         @RequestParam(value = "image", required = false) String image,
                         @RequestParam(value = "appName", required = false) String appName) {
    return opsMxSsdService.getSddResponse5(version, type, source, source1, source2, source3, appId, image, appName)
  }

  @ApiOperation(value = "Endpoint for ssd services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}", method = RequestMethod.GET)
  Object getSsdResponse5(@PathVariable("version") String version,
                         @PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @PathVariable("source1") String source1,
                         @PathVariable("source2") String source2,
                         @PathVariable("source3") String source3,
                         @PathVariable("source4") String source4,
                         @RequestParam(value = "appId", required = false) Integer appId,
                         @RequestParam(value = "image", required = false) String image,
                         @RequestParam(value = "appName", required = false) String appName) {
    return opsMxSsdService.getSddResponse6(version, type, source, source1, source2, source3, source4, appId, image, appName)
  }

  @ApiOperation(value = "Endpoint for ssd services")
  @RequestMapping(value = "/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}", method = RequestMethod.GET)
  Object getSsdResponse6(@PathVariable("version") String version,
                         @PathVariable("type") String type,
                         @PathVariable("source") String source,
                         @PathVariable("source1") String source1,
                         @PathVariable("source2") String source2,
                         @PathVariable("source3") String source3,
                         @PathVariable("source4") String source4,
                         @PathVariable("source5") String source5,
                         @RequestParam(value = "appId", required = false) Integer appId,
                         @RequestParam(value = "image", required = false) String image,
                         @RequestParam(value = "appName", required = false) String appName) {
    return opsMxSsdService.getSddResponse7(version, type, source, source1, source2, source3, source4, source5, appId, image, appName)
  }

  @ApiOperation(value = "Endpoint to download csv file")
  @RequestMapping(value = "/{version}/{type}/{source}/download", produces = "text/csv", method = RequestMethod.GET)
  Object downloadCSVFileAuditService(@PathVariable("version") String version,
                                     @PathVariable("type") String type,
                                     @PathVariable("source") String source,
                                     @RequestParam(value = "appId", required = false) String appId,
                                     @RequestParam(value = "image", required = false) String image,
                                     @RequestParam(value = "appName", required = false) String appName,
                                     @RequestParam(value = "account", required = false) String account) {
    Response response = opsMxSsdService.downloadCSVFile(version, type, source, appId, image, appName, account)
    log.info("response for the download csv endpoint:" + response.getHeaders())
    if (response.getBody() != null) {
      InputStream inputStream = response.getBody().in()
      try {
        byte[] csvFile = IOUtils.toByteArray(inputStream)
        HttpHeaders headers = new HttpHeaders()
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.add("Content-Disposition", response.getHeaders().stream().filter({ header -> header.getName().trim().equalsIgnoreCase("Content-Disposition") }).collect(Collectors.toList()).get(0).value)
        return ResponseEntity.ok().headers(headers).body(csvFile)
      } finally {
        if (inputStream != null) {
          inputStream.close()
        }
      }
    }
    return ResponseEntity.status(response.getStatus()).build()
  }

  @ApiOperation(value = "Endpoint to download json file")
  @RequestMapping(value = "/{version}/{type}/{source}/download/json", produces = "application/json", method = RequestMethod.GET)
  Object downloadJsonFileSsdService(@PathVariable("version") String version,
                                    @PathVariable("type") String type,
                                    @PathVariable("source") String source,
                                    @RequestParam(value = "appId", required = false) String appId,
                                    @RequestParam(value = "image", required = false) String image,
                                    @RequestParam(value = "appName", required = false) String appName,
                                    @RequestParam(value = "account", required = false) String account,
                                    @RequestParam(value = "scoreCardName", required = false) String scoreCardName){
    Response response = opsMxSsdService.downloadJsonFile(version, type, source, appId, image, appName, account, scoreCardName)
    log.info("response for the download json endpoint:" + response.getHeaders())
    if (response.getBody() != null) {
      InputStream inputStream = response.getBody().in()
      try {
        byte[] jsonFile = IOUtils.toByteArray(inputStream)
        HttpHeaders headers = new HttpHeaders()
        headers.add("Access-Control-Allow-Headers", "x-requested-with, content-type, Content-Disposition")
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.add("Content-Disposition", response.getHeaders().stream().filter({ header -> header.getName().trim().equalsIgnoreCase("Content-Disposition") }).collect(Collectors.toList()).get(0).value)
        return ResponseEntity.ok().headers(headers).body(jsonFile)
      } finally {
        if (inputStream != null) {
          inputStream.close()
        }
      }
    }
    return ResponseEntity.status(response.getStatus()).build()
  }
}
