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

package com.netflix.spinnaker.gate.services.internal

import retrofit.client.Response
import retrofit.http.*

interface OpsmxAutopilotService {

  @GET("/autopilot/canaries/debugLogsData")
  Response downloadDebugData(@Query("id") Integer canaryId)

  @GET("/autopilot/canaries/patternCompleteLog")
  Response downloadPatternCompleteLog(@Query("canaryId") Integer canaryId,
                               @Query("serviceId") Integer serviceId,
                               @Query("patternId") String patternId)

  @GET("/autopilot/canaries/clusterCompleteLog")
  Response downloadClusterInfo(@Query("canaryId") Integer canaryId,
                               @Query("serviceId") Integer serviceId,
                               @Query("clusterId") Integer clusterId,
                               @Query("version") String version,
                               @Query("testCaseId") Integer testCaseId)

  @POST("/autopilot/api/{version}/registerCanary")
  Response triggerRegisterCanary(@Path('version') String version, @Body Object data,@Header('x-spinnaker-user') String xSpinnakerUser)

  @GET("/autopilot/{type}/{source}")
  Object getAutoResponse(@Path('type') String type,
                         @Path('source') String source,
                         @Query("application") Integer id,
                         @Query("applicationId") Integer applicationId,
                         @Query("serviceId") Integer serviceId,
                         @Query("startTime") Long startTime,
                         @Query("endTime") Long endTime,
                         @Query("intervalMins") Float intervalMins,
                         @Query("limit") Integer limit,
                         @Query("sourceType") String sourceType,
                         @Query("datasourceType") String datasourceType,
                         @Query("accountName") String accountName,
                         @Query("templateType") String templateType,
                         @Query("name") String name,
                         @Query("appId") Integer appId,
                         @Query("pipelineid") String pipelineid,
                         @Query("applicationName") String applicationName,
                         @Query("username") String username,
                         @Query("userName") String userName,
                         @Query("templateName") String templateName,
                         @Query("credentialType") String credentialType,
                         @Query("id") Integer Id,
                         @Query("service") Integer service,
                         @Query("canaryId") Integer canary,
                         @Query("canaryid") Integer canaryid,
                         @Query("clusterId") Long clusterId,
                         @Query("version") String version,
                         @Query("canaryAnalysisId") Integer canaryAnalysisId,
                         @Query("metric") String metric,
                         @Query("account") String account,
                         @Query("metricType") String metricType,
                         @Query("isBoxplotData") Boolean isBoxplotData,
                         @Query("metricname") String metricname,
                         @Query("numofver") Integer numofver,
                         @Query("serviceName") String serviceName,
                         @Query("platform") String platform,
                         @Query("ruleId") Integer ruleId,
                         @Query("zone") String zone,
                         @Query("type") String appType,
                         @Query("metricTemplate") String metricTemplate,
                         @Query("logTemplate") String logTemplate,
                         @Query("riskanalysis_id") Integer riskanalysis_id,
                         @Query("service_id") Integer service_id,
                         @Query("userId") Integer userId,
                         @Query("logTemplateName") String logTemplateName,
                         @Query("forceDelete") Boolean forceDelete,
                         @Query("deleteAssociateRuns") Boolean deleteAssociateRuns,
                         @Query("event") String event,
                         @Query("serviceList") List<String> serviceList,
                         @Query("pipelineId") String pipelineId,
                         @Query("referer") String referer,
                         @Query("testCaseId") Integer testCaseId,
                         @Query("verificationType") String verificationType,
                         @Query("patternId") String patternId)

  @GET("/autopilot/{type}")
  Object getAutoResponse1(@Path('type') String type)

  @GET("/autopilot/{type}/{source}/{source1}")
  Object getAutoResponse4(@Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Query("Ids") String[] applicationsIds,
                          @Query("datasourceType") String datasourceType,
                          @Query("canaryId") Integer canaryId,
                          @Query("serviceId") Integer serviceId,
                          @Query("pageNo") Integer pageNo,
                          @Query("pageLimit") Integer pageLimit,
                          @Query("sortBy") String sortBy,
                          @Query("sortOrder") String sortOrder,
                          @Query("applicationId") Integer applicationId,
                          @Query("noOfDays") Integer noOfDays,
                          @Query("patternId") String patternId,
                          @Query("search") String search,
                          @Query("verificationType") String verificationType
  )

  @GET("/autopilot/{type}/{source}/{source1}/{source2}")
  Object getAutoResponse5(@Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Path('source2') String source2,
                          @Query("canaryId") Integer canaryId,
                          @Query("verificationType") String verificationType,
                          @Query("permissionId") String permissionId)

  @GET("/autopilot/{type}/{source}/{source1}/{source2}/{source3}")
  Object getAutoResponse6(@Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Path('source2') String source2,
                          @Path('source3') String source3,
                          @Query("pageNo") Integer pageNo,
                          @Query("pageLimit") Integer pageLimit,
                          @Query("noOfDays") Integer noOfDays,
                          @Query("search") String search,
                          @Query("verificationType") String verificationType,
                          @Query("status") String status,
                          @Query("sortOrder") String sortOrder)

  @GET("/autopilot/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object getAutoResponse7(@Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Path('source2') String source2,
                          @Path('source3') String source3,
                          @Path('source4') String source4,
                          @Query("time") String time)

  @GET("/autopilot/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}")
  Object getAutoResponse8(@Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Path('source2') String source2,
                          @Path('source3') String source3,
                          @Path('source4') String source4,
                          @Path('source5') String source5,
                          @Query("imageId") String imageId,
                          @Query("canaryIds") String canaryIds,
                          @Query("gateIds") String gateIds)


  @GET("/autopilot/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}/{source6}/{source7}/{source8}")
  Object getAutoResponse9(@Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Path('source2') String source2,
                          @Path('source3') String source3,
                          @Path('source4') String source4,
                          @Path('source5') String source5,
                          @Path('source6') String source6,
                          @Path('source7') String source7,
                          @Path('source8') String source8,
                          @Query("imageId") String imageId)

  @GET("/autopilot/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}/{source6}")
  Object getAutoResponse10(@Path('type') String type,
                           @Path('source') String source,
                           @Path('source1') String source1,
                           @Path('source2') String source2,
                           @Path('source3') String source3,
                           @Path('source4') String source4,
                           @Path('source5') String source5,
                           @Path('source6') String source6,
                           @Query("imageId") String imageId,
                           @Query("canaryIds") String canaryIds,
                           @Query("gateIds") String gateIds)

  @DELETE("/autopilot/{type}")
  Object deleteAutoResponse1(@Path('type') String type)

  @DELETE("/autopilot/{type}/{source}")
  Object deleteAutoResponse(@Path('type') String type,
                           @Path('source') String source,
                           @Query("applicationId") Integer applicationId,
                           @Query("pipelineid") String pipelineid,
                           @Query("applicationName") String applicationName,
                           @Query("accountName") String accountName,
                           @Query("sourceType") String sourceType,
                           @Query("credentialType") String credentialType,
                           @Query("canaryId") Integer canaryId,
                           @Query("forceDelete") Boolean forceDelete,
                           @Query("deleteAssociateRuns") Boolean deleteAssociateRuns,
                           @Query("templateName") String templateName)

  @DELETE("/autopilot/{type}/{source}/{source1}")
  Object deleteAutoResponse3(@Path('type') String type,
                            @Path('source') String source,
                            @Path('source1') String source1)

  @DELETE("/autopilot/{type}/{source}/{source1}/{source2}")
  Object deleteAutoResponse4(@Path('type') String type,
                             @Path('source') String source,
                             @Path('source1') String source1,
                             @Path('source2') String source2)

  @DELETE("/autopilot/{type}/{source}/{source1}/{source2}/{source3}")
  Object deleteAutoResponse5(@Path('type') String type,
                             @Path('source') String source,
                             @Path('source1') String source1,
                             @Path('source2') String source2,
                             @Path('source3') String source3)

  @DELETE("/autopilot/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object deleteAutoResponse6(@Path('type') String type,
                             @Path('source') String source,
                             @Path('source1') String source1,
                             @Path('source2') String source2,
                             @Path('source3') String source3,
                             @Path('source4') String source4)

  @POST("/autopilot/{type}")
  Object postAutoResponse(@Path('type') String type,
                         @Body Object data)

  @POST("/autopilot/{type}/{source}")
  Object postAutoResponse1(@Path('type') String type,
                           @Path('source') String source,
                           @Query("isEdit") Boolean pipelineid,
                           @Query('userName') String userName,
                           @Query('userId') Integer userId,
                           @Query('canaryId') Integer canaryId,
                           @Query('logTemplateName') String logTemplateName,
                           @Query('serviceId') Integer serviceId,
                           @Query('testCaseId') Integer testCaseId,
                           @Body Object data)

  @POST("/autopilot/{type}/{source}/{source1}")
  Object postAutoResponse4(@Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Body Object data)

  @POST("/autopilot/{type}/{source}/{source1}/{source2}")
  Object postAutoResponse5(@Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Path('source2') String source2,
                           @Query("isEdit") Boolean isEdit,
                          @Body Object data)

  @POST("/autopilot/{type}/{source}/{source1}/{source2}/{source3}")
  Object postAutoResponse6(@Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Path('source2') String source2,
                          @Path('source3') String source3,
                           @Query("isEdit") Boolean isEdit,
                          @Body Object data)

  @PUT("/autopilot/{type}")
  Object updateAutopilotResponse(@Path('type') String type,
                                 @Body Object data)

  @PUT("/autopilot/{type}/{source}")
  Object updateAutopilotResponse1(@Path('type') String type,
                                  @Path('source') String source,
                                  @Body Object data)

  @PUT("/autopilot/{type}/{source}/{source1}")
  Object updateAutopilotResponse2(@Path('type') String type,
                                  @Path('source') String source,
                                  @Path('source1') String source1,
                                  @Body Object data)

  @PUT("/autopilot/{type}/{source}/{source1}/{source2}")
  Object updateAutopilotResponse3(@Path('type') String type,
                                  @Path('source') String source,
                                  @Path('source1') String source1,
                                  @Path('source2') String source2,
                                  @Body Object data)

  @PUT("/autopilot/{type}/{source}/{source1}/{source2}/{source3}")
  Object updateAutopilotResponse4(@Path('type') String type,
                                  @Path('source') String source,
                                  @Path('source1') String source1,
                                  @Path('source2') String source2,
                                  @Path('source3') String source3,
                                  @Body Object data)

  @PUT("/autopilot/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object updateAutopilotResponse5(@Path('type') String type,
                                  @Path('source') String source,
                                  @Path('source1') String source1,
                                  @Path('source2') String source2,
                                  @Path('source3') String source3,
                                  @Path('source4') String source4,
                                  @Body Object data)

  @PUT("/autopilot/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}")
  Object updateAutopilotResponse6(@Path('type') String type,
                                  @Path('source') String source,
                                  @Path('source1') String source1,
                                  @Path('source2') String source2,
                                  @Path('source3') String source3,
                                  @Path('source4') String source4,
                                  @Path('source5') String source5,
                                  @Body Object data)

}
