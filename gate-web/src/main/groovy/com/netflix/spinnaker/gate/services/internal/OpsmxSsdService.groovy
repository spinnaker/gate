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

package com.netflix.spinnaker.gate.services.internal

import org.springframework.web.bind.annotation.RequestParam
import retrofit.client.Response
import retrofit.http.GET
import retrofit.http.Path
import retrofit.http.Query

interface OpsmxSsdService {
  @GET("/ssdservice/{version}/{type}")
  Object getSddResponse1(@Path('version') String version,
                         @Path('type') String type,
                         @Query("account") String account,
                         @Query("appId") Integer appId,
                         @Query("image") String image,
                         @Query("imageTag") String imageTag,
                         @Query('stage') String stage,
                         @Query("deployedAt") String deployedAt,
                         @Query("appName") String appName,
                         @Query("pageNo") Integer pageNo,
                         @Query("pageLimit") Integer pageLimit,
                         @Query("sortBy") String sortBy,
                         @Query("sortOrder") String sortOrder,
                         @Query("search") String search,
                         @Query("noOfDays") Integer noOfDays,
                         @Query("policy") String policy,
                         @Query("typeList") String typeList,
                         @Query("alertName") String alertName)

  @GET("/ssdservice/{version}/{type}/{source}")
  Object getSddResponse2(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source,
                         @Query("account") String account,
                         @Query("appId") Integer appId,
                         @Query("image") String image,
                         @Query("imageTag") String imageTag,
                         @Query("stage") String stage,
                         @Query("deployedAt") String deployedAt,
                         @Query("appName") String appName,
                         @Query("pageNo") Integer pageNo,
                         @Query("pageLimit") Integer pageLimit,
                         @Query("sortBy") String sortBy,
                         @Query("sortOrder") String sortOrder,
                         @Query("search") String search,
                         @Query("noOfDays") Integer noOfDays,
                         @Query("alertName") String alertName,
                         @Query("riskStatus") String riskStatus)

  @GET("/ssdservice/{version}/{type}/{source}/{source1}")
  Object getSddResponse3(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Query("appId") Integer appId,
                         @Query("image") String image,
                         @Query("appName") String appName)

  @GET("/ssdservice/{version}/{type}/{source}/{source1}/{source2}")
  Object getSddResponse4(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Path('source2') String source2,
                         @Query("account") String account,
                         @Query("appId") Integer appId,
                         @Query("image") String image,
                         @Query("imageTag") String imageTag,
                         @Query("stage") String stage,
                         @Query("deployedAt") String deployedAt,
                         @Query("appName") String appName,
                         @Query("pageNo") Integer pageNo,
                         @Query("pageLimit") Integer pageLimit,
                         @Query("sortBy") String sortBy,
                         @Query("sortOrder") String sortOrder,
                         @Query("search") String search,
                         @Query("noOfDays") Integer noOfDays)

  @GET("/ssdservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object getSddResponse5(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Path('source2') String source2,
                         @Path('source3') String source3,
                         @Query("appId") Integer appId,
                         @Query("image") String image,
                         @Query("appName") String appName)

  @GET("/ssdservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}")
  Object getSddResponse6(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Path('source2') String source2,
                         @Path('source3') String source3,
                         @Path('source4') String source4,
                         @Query("appId") Integer appId,
                         @Query("image") String image,
                         @Query("appName") String appName)

  @GET("/ssdservice/{version}/{type}/{source}/{source1}/{source2}/{source3}/{source4}/{source5}")
  Object getSddResponse7(@Path('version') String version,
                         @Path('type') String type,
                         @Path('source') String source,
                         @Path('source1') String source1,
                         @Path('source2') String source2,
                         @Path('source3') String source3,
                         @Path('source4') String source4,
                         @Path('source5') String source5,
                         @Query("appId") Integer appId,
                         @Query("image") String image,
                         @Query("appName") String appName)

  @GET("/ssdservice/{version}/{type}/{source}/download")
  Response downloadCSVFile(@Path('version') String version,
                           @Path('type') String type,
                           @Path('source') String source,
                           @Query("appId") Integer appId,
                           @Query("image") String image,
                           @Query("appName") String appName)

}