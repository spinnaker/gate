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

import com.netflix.spinnaker.fiat.model.resources.ServiceAccount
import retrofit.client.Response
import retrofit.http.*

interface Front50Service {
  @GET("/credentials")
  List<Map> getCredentials()

  //
  // Application-related
  //
  @GET('/v2/applications?restricted=false')
  List<Map> getAllApplicationsUnrestricted()

  @GET('/v2/applications/{applicationName}')
  Map getApplication(@Path('applicationName') String applicationName)

  @GET('/v2/applications/{applicationName}/history')
  List<Map> getApplicationHistory(@Path("applicationName") String applicationName, @Query("limit") int limit)

  //
  // Pipeline-related
  //
  @GET('/pipelines')
  List<Map> getAllPipelineConfigs()

  @GET('/pipelines/{app}')
  List<Map> getPipelineConfigsForApplication(@Path("app") String app, @Query("refresh") boolean refresh)

  @DELETE('/pipelines/{app}/{name}')
  Response deletePipelineConfig(@Path("app") String app, @Path("name") String name)

  @POST('/pipelines')
  Response savePipelineConfig(@Body Map pipelineConfig)

  @POST('/pipelines/move')
  Response movePipelineConfig(@Body Map moveCommand) //TODO: remove this when deck & front50 are updated

  @GET('/pipelines/{pipelineConfigId}/history')
  List<Map> getPipelineConfigHistory(@Path("pipelineConfigId") pipelineConfigId, @Query("limit") int limit)

  @PUT("/pipelines/{pipelineId}")
  Map updatePipeline(@Path("pipelineId") String pipelineId, @Body Map pipeline)

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

  @PUT("/strategies/{strategyId}")
  Map updateStrategy(@Path("strategyId") String strategyId, @Body Map strategy)

  //
  // Pipeline Template-related
  //
  @GET('/pipelineTemplates')
  List<Map> getPipelineTemplates(@Query("scopes") String... scopes)

  @GET('/pipelineTemplates/{pipelineTemplateId}')
  Map getPipelineTemplate(@Path("pipelineTemplateId") String pipelineTemplateId)

  @GET("/pipelineTemplates/{pipelineTemplateId}/dependentPipelines")
  List<Map<String, Object>> getPipelineTemplateDependents(@Path("pipelineTemplateId") String pipelineTemplateId,
                                                          @Query("recursive") boolean recursive)

  // v2
  @GET('/v2/pipelineTemplates/{pipelineTemplateId}')
  Map getV2PipelineTemplate(@Path("pipelineTemplateId") String pipelineTemplateId,
                            @Query("tag") String tag,
                            @Query("digest") String digest)

  // TODO(louisjimenez): Deprecated. Will be replaced with /versions endpoint starting with 1.19.
  @GET('/v2/pipelineTemplates')
  List<Map> getV2PipelineTemplates(@Query("scopes") String... scopes)

  @GET('/v2/pipelineTemplates/versions')
  Map<String, List<Map>> getV2PipelineTemplatesVersions(@Query("scopes") String... scopes)

  @GET("/v2/pipelineTemplates/{pipelineTemplateId}/dependentPipelines")
  List<Map<String, Object>> getV2PipelineTemplateDependents(@Path("pipelineTemplateId") String pipelineTemplateId)

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
  List<Map> getAllProjects()

  @GET('/v2/projects/{projectId}')
  Map getProject(@Path('projectId') String projectId)

  //
  // Snapshot-related
  //
  @GET('/snapshots/{id}')
  Map getCurrentSnapshot(@Path('id') String id)

  @GET('/snapshots/{id}/history')
  List<Map> getSnapshotHistory(@Path("id") String id, @Query("limit") int limit)

  @GET('/serviceAccounts')
  List<ServiceAccount> getServiceAccounts()

  //
  // Delivery-related
  //
  @GET('/deliveries')
  List<Map> getDeliveries()

  @GET('/deliveries/{id}')
  Map getDelivery(@Path('id') String id)

  // Plugins related
  @GET('/pluginInfo')
  List<Map> getPluginInfo(@Query("service") String service)
}
