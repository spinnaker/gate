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


package com.netflix.spinnaker.gate.services

import com.netflix.spinnaker.gate.config.GateConfig
import com.netflix.spinnaker.gate.config.ServiceConfiguration
import com.netflix.spinnaker.gate.services.internal.Front50Service
import com.netflix.spinnaker.kork.web.exceptions.InvalidRequestException
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.RequestBody
import com.squareup.okhttp.Response
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import retrofit.Endpoint

@CompileStatic
@Component
@Slf4j
class NotificationService {
  @Autowired(required = false)
  Front50Service front50Service

  @Autowired(required = false)
  OkHttpClient okHttpClient

  @Autowired
  ServiceConfiguration serviceConfiguration

  Map getNotificationConfigs(String type, String app) {
    front50Service.getNotificationConfigs(type, app)
  }

  void saveNotificationConfig(String type, String app, Map notification) {
    front50Service.saveNotificationConfig(type, app, notification)
  }

  void deleteNotificationConfig(String type, String app) {
    front50Service.deleteNotificationConfig(type, app)
  }

  ResponseEntity<String> processNotificationCallback(String source, RequestEntity<String> request) {
    String accept = request.getHeaders().getFirst("Accept")?.toLowerCase()
    String contentType = request.getHeaders().getFirst("Content-Type")?.toLowerCase()

    if (!contentType) {
      throw new InvalidRequestException("No Content-Type header present in request. Unable to process notification callback.")
    }

    final MediaType mediaType = MediaType.parse(contentType)

    // We use the "raw" OkHttpClient here instead of a RestAdapter because retrofit messes up with the encoding
    // of the body for the x-www-form-urlencoded content type, which is what Slack uses. This allows us to pass
    // the original body unmodified along to echo.
    Endpoint echoEndpoint = GateConfig.createEndpoint(serviceConfiguration, "echo")

    Request echoRequest = new Request.Builder()
      .url(echoEndpoint.url + request.url.path)
      .post(RequestBody.create(mediaType, request.body))
      .header("Accept", accept)
      .build();

    Response response = okHttpClient.newCall(echoRequest).execute()
    String body = response.body().string()
    Map headers = response.headers().toMultimap()

    return new ResponseEntity(body, new HttpHeaders(headers as MultiValueMap), HttpStatus.valueOf(response.code()))
  }
}
