/*
 * Copyright 2019 Netflix, Inc.
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

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Provides a shim to migrate users from Spring Boot 1 Security configs to Spring Boot 2 way of doing things.
 */
@Getter
@Setter
@ConfigurationProperties("security")
public class SpringBoot1SecurityShimProperties {

  private BasicProperties basic = new BasicProperties();

  @Getter
  @Setter
  public static class BasicProperties {
    private boolean enabled;
  }
}
