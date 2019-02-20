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

package com.netflix.spinnaker.gate.config.gremlin

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spectator.api.Registry
import com.netflix.spinnaker.gate.config.Service
import com.netflix.spinnaker.gate.config.ServiceConfiguration
import com.netflix.spinnaker.gate.retrofit.EurekaOkClient
import com.netflix.spinnaker.gate.retrofit.Slf4jRetrofitLogger
import com.netflix.spinnaker.gate.services.EurekaLookupService
import com.netflix.spinnaker.gate.services.gremlin.GremlinService
import com.squareup.okhttp.OkHttpClient
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit.Endpoint
import retrofit.RequestInterceptor
import retrofit.RestAdapter
import retrofit.converter.JacksonConverter

import static retrofit.Endpoints.newFixedEndpoint

@Slf4j
@CompileStatic
@Configuration
@ConditionalOnProperty('integrations.gremlin.enabled')
class GremlinConfig {
  @Value('${retrofit.logLevel:BASIC}')
  String retrofitLogLevel

  @Autowired
  ServiceConfiguration serviceConfiguration

  @Autowired
  Registry registry

  @Autowired
  EurekaLookupService eurekaLookupService

  @Autowired
  RequestInterceptor spinnakerRequestInterceptor

  @Bean
  GremlinService gremlinService(OkHttpClient okHttpClient) {
    createClient('gremlin', GremlinService, okHttpClient)
  }

  private <T> T createClient(String serviceName,
                             Class<T> type,
                             OkHttpClient okHttpClient) {
    Service service = serviceConfiguration.getService(serviceName)
    if (service == null) {
      throw new IllegalArgumentException("Unknown service ${serviceName} requested of type ${type}")
    }

    if (!service.enabled) {
      return null
    }

    Endpoint endpoint = newFixedEndpoint(service.baseUrl)

    def client = new EurekaOkClient(okHttpClient, registry, serviceName, eurekaLookupService)
    buildService(client, type, endpoint)
  }

  private <T> T buildService(EurekaOkClient client, Class<T> type, Endpoint endpoint) {
    ObjectMapper objectMapper = new ObjectMapper()
      .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    new RestAdapter.Builder()
      .setRequestInterceptor(spinnakerRequestInterceptor)
      .setEndpoint(endpoint)
      .setClient(client)
      .setConverter(new JacksonConverter(objectMapper))
      .setLogLevel(RestAdapter.LogLevel.valueOf(retrofitLogLevel))
      .setLog(new Slf4jRetrofitLogger(type))
      .build()
      .create(type)
  }
}
