/*
 * Copyright 2021 OpsMx, Inc.
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

package com.netflix.spinnaker.gate.config;

import static com.opsmx.spinnaker.gate.constant.CamelEndpointConstant.directUserActivity;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression("${message-broker.enabled:true}")
public class UserActivityRouteBuilder extends RouteBuilder {

  private final String userActivity = "userActivity";
  @Autowired private CamelRouteConfig camelRouteConfig;

  @Override
  public void configure() throws Exception {

    from(directUserActivity)
        .id(userActivity)
        .to(camelRouteConfig.getUserActivityQueueEndPoint())
        .end();
  }
}
