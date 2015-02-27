/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.gate.services.internal

import retrofit.http.*

interface OrcaService {

  @Headers("Content-type: application/context+json")
  @POST("/ops")
  Map doOperation(@Body Map<String, ? extends Object> body)

  @Headers("Accept: application/json")
  @GET("/applications/{application}/tasks")
  List getTasks(@Path("application") String app, @Query("page") Integer pageNumber, @Query("pageSize") Integer pageSize)

  @Headers("Accept: application/json")
  @GET("/applications/{application}/pipelines")
  List getPipelines(@Path("application") String app)

  @Headers("Accept: application/json")
  @GET("/tasks/{id}")
  Map getTask(@Path("id") String id)

  @Headers("Accept: application/json")
  @GET("/tasks")
  Map all()

  @Headers("Accept: application/json")
  @PUT("/tasks/{id}/cancel")
  Map cancelTask(@Path("id") String id)

  @Headers("Accept: application/json")
  @PUT("/pipelines/{id}/cancel")
  Map cancelPipeline(@Path("id") String id)

  @Headers("Accept: application/json")
  @POST("/orchestrate")
  Map startPipeline(@Body Map pipelineConfig, @Query("user") String user)
}
