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

import retrofit.http.GET
import retrofit.http.Path

interface OpsmxAuditClientService {

  @GET("/auditclientservice/{version}/{type}")
  Object getAuditClientResponse1(@Path('version') String version,
                              @Path('type') String type)

  @GET("/auditclientservice/{version}/{type}/{source}")
  Object getAuditClientResponse2(@Path('version') String version,
                             @Path('type') String type,
                             @Path('source') String source)

  @GET("/auditclientservice/{version}/{type}/{source}/{source1}")
  Object getAuditClientResponse3(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1)

  @GET("/auditclientservice/{version}/{type}/{source}/{source1}/{source2}")
  Object getAuditClientResponse4(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Path('source2') String source2)

  @GET("/auditclientservice/{version}/{type}/{source}/{source1}/{source2}/{source3}")
  Object getAuditClientResponse5(@Path('version') String version,
                              @Path('type') String type,
                              @Path('source') String source,
                              @Path('source1') String source1,
                              @Path('source2') String source2,
                              @Path('source3') String source3)

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

}
