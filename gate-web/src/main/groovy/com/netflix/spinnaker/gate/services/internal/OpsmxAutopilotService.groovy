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

import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import retrofit.http.*

interface OpsmxAutopilotService {

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
                         @Query("accountName") String accountName,
                         @Query("templateType") String templateType,
                         @Query("name") String name,
                         @Query("appId") Integer appId,
                         @Query("pipelineid") String pipelineid,
                         @Query("applicationName") String applicationName,
                         @Query("username") String username,
                         @Query("templateName") String templateName,
                         @Query("credentialType") String credentialType,
                         @Query("id") Integer canaryId,
                         @Query("service") Integer service,
                         @Query("canaryId") Integer canary,
                         @Query("clusterId") Long clusterId,
                         @Query("version") String version)

  @GET("/autopilot/{type}/{source}/{source1}")
  Object getAutoResponse4(@Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1)

  @GET("/autopilot/{type}/{source}/{source1}/{source2}")
  Object getAutoResponse5(@Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Path('source2') String source2)

  @GET("/autopilot/{type}/{source}/{source1}/{source2}/{source3}")
  Object getAutoResponse6(@Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Path('source2') String source2,
                         @Path('source3') String source3)

  @DELETE("/autopilot/{type}/{source}")
  Object deleteAutoResponse(@Path('type') String type,
                           @Path('source') String source)

  @DELETE("/autopilot/{type}/{source}/{source1}")
  Object deleteAutoResponse4(@Path('type') String type,
                            @Path('source') String source,
                            @Path('source1') String source1)

  @POST("/autopilot/{type}")
  Object postAutoResponse(@Path('type') String type,
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
                          @Body Object data)

  @POST("/autopilot/{type}/{source}/{source1}/{source2}/{source3}")
  Object postAutoResponse6(@Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Path('source2') String source2,
                          @Path('source3') String source3,
                          @Body Object data)

}
