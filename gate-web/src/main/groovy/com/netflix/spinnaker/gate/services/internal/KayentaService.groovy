/*
 * Copyright 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

interface KayentaService {
  @GET("/credentials")
  List getCredentials()

  @GET("/canaryConfig")
  List getCanaryConfigs(@Query("application") String application)

  @GET("/canaryConfig/{id}")
  Map getCanaryConfig(@Path("id") String id)

  @POST("/canaryConfig")
  Response createCanaryConfig(@Body Map config)

  @PUT("/canaryConfig/{id}")
  Response updateCanaryConfig(@Path("id") String id, @Body Map config)

  @DELETE("/canaryConfig/{id}")
  Response deleteCanaryConfig(@Path("id") String id)

  @GET("/judges")
  List listJudges()

  @GET("/canaryJudgeResult")
  List listResults()

  @GET("/canaryJudgeResult/{id}")
  Map getResult(@Path("id") String id)
}
