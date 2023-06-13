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


import retrofit.http.*

interface OpsmxDashboardService {

  @GET("/dashboardservice/{version}/{type}")
  Object getDashboardResponse1(@Path('version') String version,
                               @Path('type') String type,
                               @Query("datasourceType") String datasourceType,
                               @Query("pageNo") Integer pageNo,
                               @Query("pageLimit") Integer pageLimit,
                               @Query("search") String search)

  @GET("/dashboardservice/{version}/{type}/{source}")
  Object getDashboardResponse(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Query("pageNo") Integer pageNo,
                              @Query("pageLimit") Integer pageLimit,
                              @Query("search") String search)

  @GET("/dashboardservice/{version}/{type}/{source}/{source1}")
  Object getDashboardResponse4(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Path('source1') String source1,
                               @Query("pageNo") Integer pageNo,
                               @Query("pageLimit") Integer pageLimit,
                               @Query("sortBy") String sortBy,
                               @Query("sortOrder") String sortOrder,
                               @Query("search") String search,
                               @Query("noOfDays") Integer noOfDays)

  @GET("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}")
  Object getDashboardResponse5(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Path('source1') String source1,
                               @Path('source2') String source2,
                               @Query("pageNo") Integer pageNo,
                               @Query("pageLimit") Integer pageLimit,
                               @Query("sortBy") String sortBy,
                               @Query("sortOrder") String sortOrder,
                               @Query("noOfDays") Integer noOfDays)

  @GET("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object getDashboardResponse6(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Path('source2') String source2,
                         @Path('source3') String source3,
                               @Query("noOfDays") Integer noOfDays)

  @GET("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object getDashboardResponse7(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Path('source1') String source1,
                               @Path('source2') String source2,
                               @Path('source3') String source3,
                               @Path('source4') String source4,
                               @Query("pageNo") Integer pageNo,
                               @Query("pageLimit") Integer pageLimit,
                               @Query("sortBy") String sortBy,
                               @Query("sortOrder") String sortOrder)

  @GET("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}")
  Object getDashboardResponse8(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Path('source1') String source1,
                               @Path('source2') String source2,
                               @Path('source3') String source3,
                               @Path('source4') String source4,
                               @Path('source5') String source5)

  @GET("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}/{source6}")
  Object getDashboardResponse9(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Path('source1') String source1,
                               @Path('source2') String source2,
                               @Path('source3') String source3,
                               @Path('source4') String source4,
                               @Path('source5') String source5,
                               @Path('source6') String source6)

  @GET("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}/{source6}/{source7}")
  Object getDashboardResponse10(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Path('source1') String source1,
                               @Path('source2') String source2,
                               @Path('source3') String source3,
                               @Path('source4') String source4,
                               @Path('source5') String source5,
                               @Path('source6') String source6,
                                @Path('source7') String source7)

  @DELETE("/dashboardservice/{version}/{type}")
  Object deleteDashboardResponse(@Path('version') String version,
                           @Path('type') String type)

  @DELETE("/dashboardservice/{version}/{type}/{source}")
  Object deleteDashboardResponse1(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source)

  @DELETE("/dashboardservice/{version}/{type}/{source}/{source1}")
  Object deleteDashboardResponse4(@Path('version') String version,
                            @Path('type') String type,
                            @Path('source') String source,
                            @Path('source1') String source1)

  @DELETE("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}")
  Object deleteDashboardResponse5(@Path('version') String version,
                                  @Path('type') String type,
                                  @Path('source') String source,
                                  @Path('source1') String source1,
                                  @Path('source2') String source2)

  @DELETE("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object deleteDashboardResponse6(@Path('version') String version,
                                  @Path('type') String type,
                                  @Path('source') String source,
                                  @Path('source1') String source1,
                                  @Path('source2') String source2,
                                  @Path('source3') String source3)

  @DELETE("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object deleteDashboardResponse7(@Path('version') String version,
                                  @Path('type') String type,
                                  @Path('source') String source,
                                  @Path('source1') String source1,
                                  @Path('source2') String source2,
                                  @Path('source3') String source3,
                                  @Path('source4') String source4,
                                  @Header('x-user-cookie') String cookie)

  @DELETE("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}")
  Object deleteDashboardResponse8(@Path('version') String version,
                                  @Path('type') String type,
                                  @Path('source') String source,
                                  @Path('source1') String source1,
                                  @Path('source2') String source2,
                                  @Path('source3') String source3,
                                  @Path('source4') String source4,
                                  @Path('source5') String source5)

  @POST("/dashboardservice/{version}/{type}")
  Object postDashboardResponse(@Path('version') String version,
                         @Path('type') String type,
                         @Body Object data)

  @POST("/dashboardservice/{version}/{type}/{source}")
  Object postDashboardResponse3(@Path('version') String version,
                               @Path('type') String type,
                               @Path('source') String source,
                               @Body Object data)

  @POST("/dashboardservice/{version}/{type}/{source}/{source1}")
  Object postDashboardResponse4(@Path('version') String version,
                          @Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                                @Header('x-user-cookie') String cookie,
                          @Header('x-spinnaker-user') String user,
                          @Body Object data)

  @POST("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}")
  Object postDashboardResponse5(@Path('version') String version,
                          @Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Path('source2') String source2,
                          @Body Object data)

  @POST("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object postDashboardResponse6(@Path('version') String version,
                          @Path('type') String type,
                          @Path('source') String source,
                          @Path('source1') String source1,
                          @Path('source2') String source2,
                          @Path('source3') String source3,
                          @Body Object data)

  @POST("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object postDashboardResponse7(@Path('version') String version,
                                @Path('type') String type,
                                @Path('source') String source,
                                @Path('source1') String source1,
                                @Path('source2') String source2,
                                @Path('source3') String source3,
                                @Path('source4') String source4,
                                @Body Object data)

  @POST("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}")
  Object postDashboardResponse8(@Path('version') String version,
                                @Path('type') String type,
                                @Path('source') String source,
                                @Path('source1') String source1,
                                @Path('source2') String source2,
                                @Path('source3') String source3,
                                @Path('source4') String source4,
                                @Path('source5') String source5,
                                @Body Object data)

  @PUT("/dashboardservice/{version}/{type}")
  Object updateDashboardResponse(@Path('version') String version,
                           @Path('type') String type,
                           @Body Object data)

  @PUT("/dashboardservice/{version}/{type}/{source}")
  Object updateDashboardResponse1(@Path('version') String version,
                                @Path('type') String type,
                                @Path('source') String source,
                                @Body Object data)

  @PUT("/dashboardservice/{version}/{type}/{source}/{source1}")
  Object updateDashboardResponse2(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Body Object data)

  @PUT("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}")
  Object updateDashboardResponse3(@Path('version') String version,
                                  @Path('type') String type,
                                  @Path('source') String source,
                                  @Path('source1') String source1,
                                  @Path('source2') String source2,
                                  @Body Object data,
                                  @Header('x-user-cookie') String cookie,
                                  @Header('x-spinnaker-user') String user)

  @PUT("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object updateDashboardResponse4(@Path('version') String version,
                                  @Path('type') String type,
                                  @Path('source') String source,
                                  @Path('source1') String source1,
                                  @Path('source2') String source2,
                                  @Path('source3') String source3,
                                  @Body Object data)

  @PUT("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object updateDashboardResponse5(@Path('version') String version,
                                  @Path('type') String type,
                                  @Path('source') String source,
                                  @Path('source1') String source1,
                                  @Path('source2') String source2,
                                  @Path('source3') String source3,
                                  @Path('source4') String source4,
                                  @Body Object data)

  @PUT("/dashboardservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}")
  Object updateDashboardResponse6(@Path('version') String version,
                                  @Path('type') String type,
                                  @Path('source') String source,
                                  @Path('source1') String source1,
                                  @Path('source2') String source2,
                                  @Path('source3') String source3,
                                  @Path('source4') String source4,
                                  @Path('source5') String source5,
                                  @Body Object data)

}
