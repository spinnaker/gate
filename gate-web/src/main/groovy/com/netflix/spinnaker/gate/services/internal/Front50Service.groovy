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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import retrofit.client.Response
import retrofit.http.*

interface Front50Service {
  @GET("/credentials")
  List<Map> getCredentials()

  //
  // Application-related
  //
  @GET('/default/applications')
  List<Map> getAllApplications()

  @GET('/default/applications/name/{name}')
  Map getApplication(@Path('name') String name)

  @GET('/default/applications/{applicationName}/history')
  List<Map> getApplicationHistory(@Path("applicationName") String applicationName, @Query("limit") int limit)

  //
  // Pipeline-related
  //
  @GET('/pipelines')
  List<Map> getAllPipelineConfigs()

  @GET('/pipelines/{app}')
  List<Map> getPipelineConfigsForApplication(@Path("app") String app)

  @DELETE('/pipelines/{app}/{name}')
  Response deletePipelineConfig(@Path("app") String app, @Path("name") String name)

  @POST('/pipelines')
  Response savePipelineConfig(@Body Map pipelineConfig)

  @POST('/pipelines/batchUpdate')
  Response batchUpdatePipelineConfig(@Body List<Map> pipelineConfig)

  @POST('/pipelines/move')
  Response movePipelineConfig(@Body Map moveCommand)

  @GET('/pipelines/{pipelineConfigId}/history')
  List<Map> getPipelineConfigHistory(@Path("pipelineConfigId") pipelineConfigId, @Query("limit") int limit)

  //
  // Pipeline Strategy-related
  //
  @GET('/strategies')
  List<Map> getAllStrategyConfigs()

  @GET('/strategies/{app}')
  List<Map> getStrategyConfigs(@Path("app") String app)

  @DELETE('/strategies/{app}/{name}')
  Response deleteStrategyConfig(@Path("app") String app, @Path("name") String name)

  @POST('/strategies')
  Response saveStrategyConfig(@Body Map strategyConfig)

  @POST('/strategies/move')
  Response moveStrategyConfig(@Body Map moveCommand)

  @GET('/strategies/{strategyConfigId}/history')
  List<Map> getStrategyConfigHistory(@Path("strategyConfigId") strategyConfigId,
                                     @Query("limit") int limit)

  //
  // Notification-related
  //
  @GET('/notifications/{type}/{app}')
  Map getNotificationConfigs(@Path('type') String type, @Path('app') String app)

  @DELETE('/notifications/{type}/{app}')
  Response deleteNotificationConfig(@Path('type') String type, @Path('app') String app)

  @POST('/notifications/{type}/{app}')
  Response saveNotificationConfig(@Path('type') String type, @Path('app') String app, @Body Map notificationConfig)

  //
  // Project-related
  //
  @GET('/v2/projects')
  HalList getAllProjects()

  @GET('/v2/projects/{projectId}')
  Map getProject(@Path('projectId') String projectId)

  @JsonIgnoreProperties(ignoreUnknown = true)
  static class HalList {
    @JsonProperty("_embedded")
    Map<String, List<Map>> embedded
  }
}
