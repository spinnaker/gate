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

package com.netflix.spinnaker.gate.security.saml

import com.netflix.spinnaker.gate.security.AuthConfig

import groovy.util.logging.Slf4j
import org.opensaml.DefaultBootstrap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices
import org.springframework.stereotype.Component

@ConditionalOnExpression('${auth.saml.enabled:false}')
@Configuration
@Slf4j
class SAMLConfig {
  @Component
  @ConfigurationProperties("auth.saml")
  static class SAMLSecurityConfigProperties {
    Boolean enabled
    String url
    String certificate
    String redirectBase

    String issuerId
    File keyStore
    String keyStoreType = 'JKS'
    String keyStorePassword
    String keyStoreAliasName

    List<String> requiredRoles
    UserAttributeMapping userAttributeMapping = new UserAttributeMapping()
  }

  static class UserAttributeMapping {
    String firstName = "User.FirstName"
    String lastName = "User.LastName"
    String roles = "memberOf"
  }

  @Autowired
  SAMLSecurityConfigProperties samlSecurityConfigProperties

  void configure(HttpSecurity http,
                 UserDetailsService userDetailsService,
                 AuthenticationManager authenticationManager) {
    org.opensaml.Configuration.validateJCEProviders()
    DefaultBootstrap.bootstrap()

    http.rememberMe().rememberMeServices(rememberMeServices(userDetailsService))
  }

  void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
    // do nothing
  }

  @Bean
  public RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
    TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices("password", userDetailsService)
    rememberMeServices.setCookieName("cookieName")
    rememberMeServices.setParameter("rememberMe")
    rememberMeServices
  }
}
