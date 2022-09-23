/*
 * Copyright 2021 OpsMx, Inc.
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

package com.opsmx.spinnaker.gate.services

import org.springframework.web.bind.annotation.RequestParam
import retrofit.client.Response
import retrofit.http.GET
import retrofit.http.Path
import retrofit.http.Query

interface OpsmxAuditClientService {

  @GET("/auditclientservice/{version}/{type}")
  Object getAuditClientResponse1(@Path('version') String version,
                              @Path('type') String type,
                                 @Query("applicationName") String applicationName,
                                 @Query("noOfDays") Integer noOfDays,
                                 @Query("pageNo") Integer page,
                                 @Query("size") Integer size,
                                 @Query("policyStatus") String policyStatus,
                                 @Query("search") String search)

  @GET("/auditclientservice/{version}/{type}/{source}")
  Object getDeliveryInsightCharts(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Query('chartId') Integer chartId,
                                 @Query('startTime') Long startTime,
                                 @Query('endTime') Long endTime,
                                 @Query('days') Integer days,
                                 @Query("noOfDays") String noOfDays,
                                 @Query("argoName") String argoName,
                                 @Query("search") String search,
                                 @Query("sort") String sort,
                                 @Query("pageNo") Integer page,
                                 @Query("pageLimit") Integer pageLimit,
                                 @Query("sortBy") String sortBy,
                                 @Query("limit") Integer limit)

  @GET("/auditclientservice/{version}/{type}/{source}/{source1}")
  Object getAuditClientResponse3(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Query("isTreeView") Boolean isTreeView,
                                 @Query("isLatest") Boolean isLatest,
                                 @Query("pageNo") Integer pageNo,
                                 @Query("pageLimit") Integer pageLimit,
                                 @Query("noOfDays") String noOfDays,
                                 @Query("search") String search)

  @GET("/auditclientservice/{version}/{type}/{source}/{source1}/{source2}")
  Object getAuditClientResponse4(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Path('source2') String source2,
                              @Query("noOfDays") String noOfDays)

  @GET("/auditclientservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object getAuditClientResponse5(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Path('source2') String source2,
                              @Path('source3') String source3,
                              @Query("noOfDays") String noOfDays)

  @GET("/auditclientservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object getAuditClientResponse6(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Path('source2') String source2,
                              @Path('source3') String source3,
                              @Path('source4') String source4)

  @GET("/auditclientservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}")
  Object getAuditClientResponse7(@Path('version') String version,
                                 @Path('type') String type,
                                 @Path('source') String source,
                                 @Path('source1') String source1,
                                 @Path('source2') String source2,
                                 @Path('source3') String source3,
                                 @Path('source4') String source4,
                                 @Path('source5') String source5)

  @GET("/auditclientservice/{version}/users/{username}/{source}/download")
  Response downloadCSVFile(@Path('version') String version,
                           @Path('username') String username,
                           @Path('source') String source,
                           @Query("isTreeView") Boolean isTreeView,
                           @Query("isLatest") Boolean isLatest,
                           @Query("pageNo") Integer pageNo,
                           @Query("size") Integer size)

  @GET("/auditclientservice/{version}/{type}/{source}/download")
  Response downloadDeliveryInsightsCSVFile(@Path('version') String version,
                                         @Path('type') String type,
                                         @Path('source') String source,
                                         @Query('chartId') Integer chartId,
                                         @Query('startTime') Long startTime,
                                         @Query('endTime') Long endTime,
                                         @Query('days') Integer days,
                                         @Query('argoName') String argoName,
                                         @Query('search') String search,
                                         @Query("noOfDays") String noOfDays,
                                         @Query('limit') Integer limit)
}
