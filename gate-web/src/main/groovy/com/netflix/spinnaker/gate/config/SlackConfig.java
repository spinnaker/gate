/*
 * Copyright 2019 Netflix, Inc.
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

package com.netflix.spinnaker.gate.config;

import static retrofit.Endpoints.newFixedEndpoint;

import com.netflix.spinnaker.gate.services.SlackService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit.Endpoint;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.JacksonConverter;

@Configuration
@ConditionalOnProperty("slack.token")
public class SlackConfig {

  @Value("${slack.token}")
  String token;

  @Bean
  Endpoint slackEndpoint(@Value("${slack.base-url}") String slackBaseUrl) {
    return newFixedEndpoint(slackBaseUrl);
  }

  RequestInterceptor requestInterceptor =
      new RequestInterceptor() {
        @Override
        public void intercept(RequestInterceptor.RequestFacade request) {
          request.addHeader("Authorization", "Token token=${token}");
        }
      };

  @Bean
  SlackService slackService(Endpoint slackEndpoint) {
    return new RestAdapter.Builder()
        .setEndpoint(slackEndpoint)
        .setClient(new OkClient())
        .setConverter(new JacksonConverter())
        .setRequestInterceptor(requestInterceptor)
        .build()
        .create(SlackService.class);
  }
}
