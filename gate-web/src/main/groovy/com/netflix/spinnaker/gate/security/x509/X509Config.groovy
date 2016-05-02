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

package com.netflix.spinnaker.gate.security.x509

import com.netflix.spinnaker.gate.security.AuthConfig
import com.netflix.spinnaker.gate.security.SpinnakerAuthConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity

@ConditionalOnExpression('${spring.x509.enabled:false}')
@SpinnakerAuthConfig
@Configuration
@EnableWebMvcSecurity
class X509Config extends WebSecurityConfigurerAdapter {

  @Value('${spring.x509.subjectPrincipalRegex:}')
  String subjectPrincipalRegex

  @Autowired
  X509AuthenticationUserDetailsService x509AuthenticationUserDetailsService

  @Override
  void configure(HttpSecurity http) {
    AuthConfig.configure(http)
    http.x509().authenticationUserDetailsService(x509AuthenticationUserDetailsService)

    if (subjectPrincipalRegex) {
      http.x509().subjectPrincipalRegex(subjectPrincipalRegex)
    }
  }
}
