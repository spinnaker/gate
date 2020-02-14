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

package com.netflix.spinnaker.gate.services

import com.netflix.spinnaker.gate.config.ServiceConfiguration
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import org.springframework.http.ResponseEntity
import spock.lang.Subject
import spock.lang.Specification

import static retrofit.Endpoints.newFixedEndpoint

class NotificationServiceSpec extends Specification {
  OkHttpClient okHttpClient = Mock()
  ServiceConfiguration serviceConfiguration = Mock()
  Call echoCall = Mock()

  @Subject NotificationService notificationService

  void setup() {
    notificationService = new NotificationService(
      front50Service: null,
      okHttpClient: okHttpClient,
      serviceConfiguration: serviceConfiguration,
    )
  }

  void "relays incoming notification callbacks to echo"() {
    given: "an incoming request"
    String body = '{ "payload": "blah" }'
    HttpHeaders headers = new HttpHeaders()
    headers.add("Content-Type", "application/json")
    headers.add("single-value-header", "value1")
    headers.add("multi-value-header", "value1")
    headers.add("multi-value-header", "value2")
    RequestEntity<String> incomingRequest = new RequestEntity(
      body, headers, HttpMethod.POST, new URI("https://gate/notifications/callbacks/someSource"))

    Request expectedEchoRequest = new Request.Builder()
      .url("https://echo" + incomingRequest.url.path)
      .post(RequestBody.create(MediaType.parse("application/json"), incomingRequest.body))
      .addHeader("Content-Type", "application/json")
      .addHeader("single-value-header", "value1")
      .addHeader("multi-value-header", "value1")
      .addHeader("multi-value-header", "value2")
      .build()

    when: "a request is received for processing"
    ResponseEntity<String> response = notificationService.processNotificationCallback("someSource", incomingRequest)

    then: "obtains echo endpoint from configuration"
    1 * serviceConfiguration.getServiceEndpoint("echo") >> newFixedEndpoint("https://echo")

    and: "calls echo with an equivalent request"
    1 * okHttpClient.newCall(_) >> { arguments ->
      Request echoRequest = arguments[0]
      echoRequest.url() == expectedEchoRequest.url()
      echoRequest.method() == expectedEchoRequest.method()
      echoRequest.body() == expectedEchoRequest.body()
      echoRequest.headers() == expectedEchoRequest.headers()
      echoCall
    }
    1 * echoCall.execute() >> mockEchoResponse(expectedEchoRequest)

    and: "returns the response from echo converted as appropriate"
    response == new ResponseEntity<String>('{ "status": "ok" }', null, HttpStatus.OK)
  }

  static Response mockEchoResponse(Request request) {
    new Response.Builder()
    .request(request)
    .protocol(Protocol.HTTP_1_1)
    .code(200)
    .message("nada")
    .body(ResponseBody.create(MediaType.parse("application/json"), '{ "status": "ok" }'))
    .build()
  }
}
