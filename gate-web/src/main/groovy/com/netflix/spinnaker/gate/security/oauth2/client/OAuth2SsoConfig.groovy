/*
 * Copyright 2016 Google, Inc.
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

package com.netflix.spinnaker.gate.security.oauth2.client

import com.netflix.spinnaker.gate.security.AuthConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration
import org.springframework.cloud.security.oauth2.resource.UserInfoTokenServices
import org.springframework.cloud.security.oauth2.sso.EnableOAuth2Sso
import org.springframework.cloud.security.oauth2.sso.OAuth2SsoConfigurer
import org.springframework.cloud.security.oauth2.sso.OAuth2SsoConfigurerAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices

@Configuration
@EnableWebSecurity
@Import(SecurityAutoConfiguration)
@EnableOAuth2Sso
// Note the 4 single-quotes below - this is a raw groovy string, because SpEL and groovy
// string syntax overlap!
@ConditionalOnExpression(''''${spring.oauth2.client.clientId:}'!=""''')
class OAuth2SsoConfig extends OAuth2SsoConfigurerAdapter {

  @Override
  public void match(OAuth2SsoConfigurer.RequestMatchers matchers) {
    matchers.antMatchers('/**')
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    AuthConfig.configure(http)
  }

  /**
   * ResourceServerTokenServices is an interface used to manage access tokens. The UserInfoTokenService object is an
   * implementation of that interface that uses an access token to get the logged in user's data (such as email or
   * profile). We want to customize the Authentication object that is returned to include our list of
   * GrantedAuthorities.
   */
  @Primary
  @Bean
  ResourceServerTokenServices spinnakerAuthorityInjectedUserInfoTokenServices() {
    return new ResourceServerTokenServices() {
      @Autowired
      UserInfoTokenServices userInfoTokenServices

      @Override
      OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        OAuth2Authentication oAuth2Authentication = userInfoTokenServices.loadAuthentication(accessToken)

        // TODO(jacobkiefer): Stuff granted authorities in here.
        // make Groups API call, make into  list of GrantedAuthorities
        // new SpinnakerAuthentication ...
        // copy over relevant bits, such as user details.

        return oAuth2Authentication
      }

      @Override
      OAuth2AccessToken readAccessToken(String accessToken) {
        return userInfoTokenServices.readAccessToken(accessToken)
      }
    }
  }
}
