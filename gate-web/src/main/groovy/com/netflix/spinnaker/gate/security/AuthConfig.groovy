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

import com.netflix.spinnaker.gate.security.anonymous.AnonymousConfig
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer

import javax.servlet.Filter

@EnableWebSecurity
@Configuration
@Import(SecurityAutoConfiguration)
@Slf4j
class AuthConfig extends WebSecurityConfigurerAdapter {

  @Autowired(required = false)
  AnonymousConfig anonymousConfig

  @Autowired(required = false)
  Collection<WebSecurityAugmentor> webSecurityAugmentors = []

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable()

    webSecurityAugmentors.each {
      it.configure(http, userDetailsService(), authenticationManager())
    }

    if (!anonymousConfig?.enabled) {
      http.authorizeRequests()
          .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
          .antMatchers('/auth/**').permitAll()
          .antMatchers('/health').permitAll()
          .antMatchers('/**').authenticated()
    }
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    webSecurityAugmentors.each {
      it.configure(auth)
    }
  }

  @Bean
  public FilterRegistrationBean securityFilterChain(
      @Qualifier(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME) Filter securityFilter) {
    FilterRegistrationBean registration = new FilterRegistrationBean(securityFilter)
    registration.setOrder(0)
    registration.setName(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)
    return registration
  }

  static interface WebSecurityAugmentor {
    void configure(AuthenticationManagerBuilder authenticationManagerBuilder)

    void configure(HttpSecurity http,
                   UserDetailsService userDetailsService,
                   AuthenticationManager authenticationManager)

  }
}
