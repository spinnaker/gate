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

package com.netflix.spinnaker.gate.security.oauth2

import com.netflix.spinnaker.gate.security.WebSecurityAugmentor
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails
import org.springframework.web.client.RestTemplate

@Slf4j
@CompileStatic
@Configuration
@ConditionalOnExpression('${oauth2.enabled:false}')
class OAuth2SecurityConfig implements WebSecurityAugmentor {

  @Override
  void configure(HttpSecurity http, UserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
    http.csrf().disable()
    http.authorizeRequests()
      .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
      .antMatchers('/oauth/**').permitAll()
      .antMatchers('/health').permitAll()
      .antMatchers('/**').fullyAuthenticated()
  }

  @Override
  void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
    // do nothing
  }

  @Bean
  @ConditionalOnMissingBean(RestTemplate)
  RestTemplate restTemplate() {
    def template = new RestTemplate()
    template.getMessageConverters().add(new FormHttpMessageConverter())
    return template
  }

  static class OAuth2Configuration extends AuthorizationCodeResourceDetails {
    @Value('${spring.oauth2.resource.userInfoUri}')
    String userInfoUri

    String userAuthorizationUri
  }

  @Bean
  @ConfigurationProperties('spring.oauth2.client')
  OAuth2Configuration resource() {
    new OAuth2Configuration()
  }
}
