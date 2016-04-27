/*
 * Copyright 2015 Netflix, Inc.
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

package com.netflix.spinnaker.gate.security.anonymous

import com.netflix.spinnaker.gate.security.AnonymousAccountsService
import com.netflix.spinnaker.gate.security.AuthConfig

import com.netflix.spinnaker.security.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AnonymousAuthenticationProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter

@ConditionalOnExpression('${auth.anonymous.enabled:false}')
@Configuration
@ConfigurationProperties(prefix = "auth.anonymous")
class AnonymousConfig {
  Boolean enabled
  String key = "spinnaker-anonymous"
  String defaultEmail = "anonymous"

  @Autowired
  AnonymousAccountsService anonymousAccountsService

  void configure(HttpSecurity http, UserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
    def filter = new AnonymousAuthenticationFilter(
      // it seems like a smell that this is statically initialized with the allowedAccounts
      key,
      new User(email: defaultEmail,
               roles: ["anonymous"],
               allowedAccounts: anonymousAccountsService.getAllowedAccounts()),
      [new SimpleGrantedAuthority("anonymous")]
    )
    http.addFilter(filter)
  }

  void configure(AuthenticationManagerBuilder auth) {
    auth.authenticationProvider(new AnonymousAuthenticationProvider(key))
  }
}
