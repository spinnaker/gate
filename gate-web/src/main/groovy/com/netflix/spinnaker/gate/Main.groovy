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

package com.netflix.spinnaker.gate

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.web.SpringBootServletInitializer
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@Configuration
@ComponentScan("com.netflix.spinnaker.gate")
@EnableAutoConfiguration
class Main extends SpringBootServletInitializer {
  private static final String ENV_KEY = "netflix.environment"

  static {
    System.setProperty(ENV_KEY, System.getProperty(ENV_KEY, "test"))
    imposeSpinnakerFileConfig("onelogin.yml")
    imposeSpinnakerClasspathConfig("onelogin.yml")
  }

  static void main(String... args) {
    SpringApplication.run Main, args
  }

  @Override
  SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    builder.sources(Main)
  }

  static void imposeSpinnakerFileConfig(String file) {
    def internalConfig = new File("${System.properties['user.home']}/.spinnaker/${file}")
    if (internalConfig.exists()) {
      System.setProperty("spring.config.location", "${System.properties["spring.config.location"]},${internalConfig.canonicalPath}")
    }
  }

  static void imposeSpinnakerClasspathConfig(String resource) {
    def internalConfig = getClass().getResourceAsStream("/${resource}")
    if (internalConfig) {
      System.setProperty("spring.config.location", "${System.properties["spring.config.location"]},classpath:/${resource}")
    }
  }
}
