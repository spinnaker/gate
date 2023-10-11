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

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ConditionalOnExpression("${message-broker.enabled:true}")
public class CamelConfig {

  @Autowired private UserActivityRouteBuilder userActivityRouteBuilder;

  @Bean
  public CamelContext camelContext() throws Exception {
    CamelContext camelContext = new DefaultCamelContext();
    camelContext.addRoutes(userActivityRouteBuilder);
    camelContext.getShutdownStrategy().setShutdownNowOnTimeout(true);
    camelContext.getShutdownStrategy().setTimeout(5);
    camelContext.getShutdownStrategy().setTimeUnit(TimeUnit.SECONDS);
    camelContext.start();
    return camelContext;
  }

  @Bean
  public ProducerTemplate producerTemplate(CamelContext camelContext) {
    return camelContext.createProducerTemplate();
  }
}
