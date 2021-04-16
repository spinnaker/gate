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

import io.swagger.models.auth.In
import retrofit.client.Response
import retrofit.http.*

interface OpsmxOesService {

   @GET("/oes/accountsConfig/cloudProviders/manifestfile/{agentName}")
   Response manifestDownloadFile(@Path('agentName') String agentName)

  @POST("/oes/{source}")
  Object postOesResponse(@Path('source') String source,
                         @Body Object data)

  @GET("/oes/{type}/{source}")
  Object getOesResponse(@Path('type') String type,
                        @Path('source') String source,
                        @Query("isTreeView") boolean isTreeView,
                        @Query("isLatest") boolean isLatest,
                        @Query("applicationName") String applicationName,
                        @Query("chartId") Integer chartId,
                        @Query("imageSource") String imageSource,
                        @Query("accountName") String accountName,
                        @Query("startTime") String startTime,
                        @Query("endTime") String endTime)

  @GET("/oes/{type}/{source}/{source1}")
  Object getOesResponse4(@Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1)

  @GET("/oes/{type}/{source}/{source1}/{source2}")
  Object getOesResponse5(@Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Path('source2') String source2)

  @GET("/oes/{type}/{source}/{source1}/{source2}/{source3}")
  Object getOesResponse6(@Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Path('source2') String source2,
                         @Path('source3') String source3,
                         @Query("noOfDays") Integer noOfDays)

  @GET("/oes/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object getOesResponse7(@Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Path('source2') String source2,
                         @Path('source3') String source3,
                         @Path('source4') String source4,
                         @Query("imageId") String imageId)

  @DELETE("/oes/{type}/{source}")
  Object deleteOesResponse(@Path('type') String type,
                           @Path('source') String source,
                           @Query("accountName") String accountName)

  @DELETE("/oes/{type}/{source}/{source1}")
  Object deleteOesResponse4(@Path('type') String type,
                            @Path('source') String source,
                            @Path('source1') String source1)

  @DELETE("/oes/{type}/{source}/{source1}/{source2}")
  Object deleteOesResponse5(@Path('type') String type,
                            @Path('source') String source,
                            @Path('source1') String source1,
                            @Path('source2') String source2)

  @DELETE("/oes/{type}/{source}/{source1}/{source2}/{source3}")
  Object deleteOesResponse6(@Path('type') String type,
                            @Path('source') String source,
                            @Path('source1') String source1,
                            @Path('source2') String source2,
                            @Path('source3') String source3)

  @POST("/oes/{type}/{source}")
  Object postOesResponse(@Path('type') String type,
                         @Path('source') String source,
                         @Query("isTreeView") boolean isTreeView,
                         @Query("isLatest") boolean isLatest,
                         @Body Object data)

  @POST("/oes/{type}/{source}/{source1}")
  Object postOesResponse4(@Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Body Object data)

  @POST("/oes/{type}/{source}/{source1}/{source2}")
  Object postOesResponse5(@Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Path('source2') String source2,
                          @Body Object data)

  @POST("/oes/{type}/{source}/{source1}/{source2}/{source3}")
  Object postOesResponse6(@Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Path('source2') String source2,
                          @Path('source3') String source3,
                          @Body Object data)

  @PUT("/oes/{type}/{source}")
  Object updateOesResponse(@Path('type') String type,
                           @Path('source') String source,
                           @Body Object data)

  @PUT("/oes/{type}/{source}/{source1}")
  Object updateOesResponse4(@Path('type') String type,
                            @Path('source') String source,
                            @Path('source1') String source1,
                            @Body Object data)

  @PUT("/oes/{type}/{source}/{source1}/{source2}")
  Object updateOesResponse5(@Path('type') String type,
                            @Path('source') String source,
                            @Path('source1') String source1,
                            @Path('source2') String source2,
                            @Body Object data)

  @PUT("/oes/{type}/{source}/{source1}/{source2}/{source3}")
  Object updateOesResponse6(@Path('type') String type,
                            @Path('source') String source,
                            @Path('source1') String source1,
                            @Path('source2') String source2,
                            @Path('source3') String source3,
                            @Body Object data)
}
