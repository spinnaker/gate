/*
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package com.netflix.spinnaker.gate.security

import groovy.util.logging.Slf4j
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity

@Slf4j
class AuthConfig {

  static void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()
    http.authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .antMatchers('/auth/**').permitAll()
        .antMatchers('/health').permitAll()
        .antMatchers('/**').authenticated()
  }
}
