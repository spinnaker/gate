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

interface OpsmxVisibilityService {

  @POST("/visibilityservice/{version}/approvalGates/{id}/trigger")
  Response triggerApprovalGate(@Path('version') String version,
                               @Path('id') Integer id,
                               @Body Object data,
                               @Header('x-spinnaker-user') String xSpinnakerUser)

  @GET("/visibilityservice/{version}/{type}")
  Object getVisibilityResponse1(@Path('version') String version,
                                @Path('type') String type,
                                @Query("serviceId") Integer serviceId,
                                @Query("images") String images,
                                @Query("executionId") String executionId)

  @GET("/visibilityservice/{version}/{type}/{source}")
  Object getVisibilityResponse(@Path('version') String version,
                             @Path('type') String type,
                             @Path('source') String source,
                               @Query("source1") String source1,
                               @Query("approvalGateInstances") List<Integer> approvalgateinstances,
                               @Query("approvalGateInstanceIds") List<Integer> approvalGateInstanceIds,
                               @Query("noOfDays") String noOfDays,
                               @Query("pageNo") Integer pageNo,
                               @Query("pageLimit") Integer pageLimit,
                               @Query("search") String search)

  @GET("/visibilityservice/{version}/{type}/{source}/{source1}")
  Object getVisibilityResponse4(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Query("status") String status,
                                @Query("images") String images,
                                @Query("executionId") String executionId,
                                @Query("noOfDays") String noOfDays)

  @GET("/visibilityservice/{version}/{type}/{source}/{source1}/{source2}")
  Object getVisibilityResponse5(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Path('source2') String source2,
                                @Query("approvalGateInstances") List<Integer> approvalgateinstances)

  @GET("/visibilityservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object getVisibilityResponse6(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Path('source2') String source2,
                              @Path('source3') String source3)

  @GET("/visibilityservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object getVisibilityResponse7(@Path('version') String version,
                                @Path('type') String type,
                                @Path('source') String source,
                                @Path('source1') String source1,
                                @Path('source2') String source2,
                                @Path('source3') String source3,
                                @Path('source4') String source4)

  @GET("/visibilityservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}")
  Object getVisibilityResponse8(@Path('version') String version,
                                @Path('type') String type,
                                @Path('source') String source,
                                @Path('source1') String source1,
                                @Path('source2') String source2,
                                @Path('source3') String source3,
                                @Path('source4') String source4,
                                @Path('source5') String source5)

  @DELETE("/visibilityservice/{version}/{type}")
  Object deleteVisibilityResponse(@Path('version') String version,
                                @Path('type') String type)

  @DELETE("/visibilityservice/{version}/{type}/{source}")
  Object deleteVisibilityResponse1(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source)

  @DELETE("/visibilityservice/{version}/{type}/{source}/{source1}")
  Object deleteVisibilityResponse4(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                   @Query("datasourceName") String datasourceName)

  @DELETE("/visibilityservice/{version}/{type}/{source}/{source1}/{source2}")
  Object deleteVisibilityResponse5(@Path('version') String version,
                                   @Path('type') String type,
                                   @Path('source') String source,
                                   @Path('source1') String source1,
                                   @Path('source2') String source2)

  @DELETE("/visibilityservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object deleteVisibilityResponse6(@Path('version') String version,
                                   @Path('type') String type,
                                   @Path('source') String source,
                                   @Path('source1') String source1,
                                   @Path('source2') String source2,
                                   @Path('source3') String source3)

  @POST("/visibilityservice/{version}/{type}")
  Object postVisibilityResponse(@Path('version') String version,
                              @Path('type') String type,
                              @Body Object data)

  @POST("/visibilityservice/{version}/{type}/{source}")
  Object postVisibilityResponse3(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Body Object data)

  @POST("/visibilityservice/{version}/{type}/{source}/{source1}")
  Object postVisibilityResponse4(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Path('source1') String source1,
                               @Body Object data)

  @POST("/visibilityservice/{version}/{type}/{source}/{source1}/{source2}")
  Object postVisibilityResponse5(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Path('source1') String source1,
                               @Path('source2') String source2,
                               @Body Object data)

  @POST("/visibilityservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object postVisibilityResponse6(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Path('source1') String source1,
                               @Path('source2') String source2,
                               @Path('source3') String source3,
                               @Body Object data)

  @POST("/visibilityservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object postVisibilityResponse7(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Path('source2') String source2,
                                 @Path('source3') String source3,
                                 @Path('source4') String source4,
                                 @Body Object data)

  @POST("/visibilityservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}")
  Object postVisibilityResponse8(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Path('source2') String source2,
                                 @Path('source3') String source3,
                                 @Path('source4') String source4,
                                 @Path('source5') String source5,
                                 @Body Object data)

  @PUT("/visibilityservice/{version}/{type}")
  Object updateVisibilityResponse(@Path('version') String version,
                                @Path('type') String type,
                                @Body Object data)

  @PUT("/visibilityservice/{version}/{type}/{source}")
  Object updateVisibilityResponse1(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Body Object data)

  @PUT("/visibilityservice/{version}/{type}/{source}/{source1}")
  Object updateVisibilityResponse2(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Body Object data)

  @PUT("/visibilityservice/{version}/{type}/{source}/{source1}/{source2}")
  Object updateVisibilityResponse3(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Path('source2') String source2,
                                 @Body Object data)

  @PUT("/visibilityservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object updateVisibilityResponse4(@Path('version') String version,
                                   @Path('type') String type,
                                   @Path('source') String source,
                                   @Path('source1') String source1,
                                   @Path('source2') String source2,
                                   @Path('source3') String source3,
                                   @Body Object data)

  @PUT("/visibilityservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object updateVisibilityResponse5(@Path('version') String version,
                                   @Path('type') String type,
                                   @Path('source') String source,
                                   @Path('source1') String source1,
                                   @Path('source2') String source2,
                                   @Path('source3') String source3,
                                   @Path('source4') String source4,
                                   @Body Object data)

  @PUT("/visibilityservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}")
  Object updateVisibilityResponse6(@Path('version') String version,
                                   @Path('type') String type,
                                   @Path('source') String source,
                                   @Path('source1') String source1,
                                   @Path('source2') String source2,
                                   @Path('source3') String source3,
                                   @Path('source4') String source4,
                                   @Path('source5') String source5,
                                   @Body Object data)

}
