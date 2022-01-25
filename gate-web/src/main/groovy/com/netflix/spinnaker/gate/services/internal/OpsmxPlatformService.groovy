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

interface OpsmxPlatformService {


  @GET("/platformservice/{version}/{type}")
  Object getPlatformResponse1(@Path('version') String version,
                              @Path('type') String type,
                              @Query("datasourceType") String datasourceType,
                              @Query("accountName") String accountName,
                              @Query("source") String source,
                              @Query("permission") String permission)

  @GET("/platformservice/{version}/{type}/{source}")
  Object getPlatformResponse(@Path('version') String version,
                             @Path('type') String type,
                             @Path('source') String source,
                             @Query("source1") String source1,
                             @Query("chartId") Integer chartId,
                             @Query("noOfDays") Integer noOfDays)

  @GET("/platformservice/{version}/{type}/{source}/{source1}")
  Object getPlatformResponse4(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Query("datasourceType") String datasourceType,
                              @Query("permissionId") String permissionId)

  @GET("/platformservice/{version}/{type}/{source}/{source1}/{source2}")
  Object getPlatformResponse5(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Path('source2') String source2,
                              @Query("permissionId") String permissionId,
                              @Query("resourceType") String resourceType,
                              @Query("featureType") String featureType)

  @GET("/platformservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object getPlatformResponse6(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Path('source2') String source2,
                              @Path('source3') String source3)

  @GET("/platformservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object getPlatformResponse7(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Path('source2') String source2,
                              @Path('source3') String source3,
                              @Path('source4') String source4)

  @GET("/platformservice/{version}/insights/download")
  Response downloadCSVFile(@Path('version') String version,
                           @Query("chartId") Integer chartId,
                           @Query("noOfDays") Integer noOfDays)

  @DELETE("/platformservice/{version}/{type}")
  Object deletePlatformResponse(@Path('version') String version,
                                @Path('type') String type,
                                @Query("accountName") String accountName)

  @DELETE("/platformservice/{version}/{type}/{source}")
  Object deletePlatformResponse1(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source)

  @DELETE("/platformservice/{version}/{type}/{source}/{source1}")
  Object deletePlatformResponse4(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1)

  @DELETE("/platformservice/{version}/{type}/{source}/{source1}/{source2}")
  Object deletePlatformResponse5(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Path('source2') String source2,
                                 @Query("featureType") String featureType
  )

  @POST("/platformservice/{version}/{type}")
  Object postPlatformResponse(@Path('version') String version,
                              @Path('type') String type,
                              @Body Object data)

  @POST("/platformservice/{version}/{type}/{source}")
  Object postPlatformResponse3(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Body Object data)

  @POST("/platformservice/{version}/{type}/{source}/{source1}")
  Object postPlatformResponse4(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Path('source1') String source1,
                               @Body Object data)

  @POST("/platformservice/{version}/{type}/{source}/{source1}/{source2}")
  Object postPlatformResponse5(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Path('source1') String source1,
                               @Path('source2') String source2,
                               @Body Object data)

  @POST("/platformservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object postPlatformResponse6(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Path('source1') String source1,
                               @Path('source2') String source2,
                               @Path('source3') String source3,
                               @Body Object data)

  @PUT("/platformservice/{version}/{type}")
  Object updatePlatformResponse(@Path('version') String version,
                                @Path('type') String type,
                                @Body Object data)

  @PUT("/platformservice/{version}/{type}/{source}")
  Object updatePlatformResponse1(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Body Object data)

  @PUT("/platformservice/{version}/{type}/{source}/{source1}")
  Object updatePlatformResponse2(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Body Object data)

  @PUT("/platformservice/{version}/{type}/{source}/{source1}/{source2}")
  Object updatePlatformResponse3(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Path('source2') String source2,
                                 @Body Object data)

  @PUT("/platformservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object updatePlatformResponse4(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Path('source2') String source2,
                                 @Path('source3') String source3,
                                 @Path('source4') String source4,
                                 @Query("featureType") String featureType,
                                 @Body Object data)

}
