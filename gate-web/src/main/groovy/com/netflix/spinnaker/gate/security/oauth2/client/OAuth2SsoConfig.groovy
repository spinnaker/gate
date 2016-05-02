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

import com.netflix.spinnaker.gate.security.AnonymousAccountsService
import com.netflix.spinnaker.gate.security.AuthConfig
import com.netflix.spinnaker.gate.security.SpinnakerAuthConfig
import com.netflix.spinnaker.gate.security.rolesprovider.SpinnakerUserRolesProvider
import com.netflix.spinnaker.security.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration
import org.springframework.cloud.security.oauth2.resource.ResourceServerProperties
import org.springframework.cloud.security.oauth2.resource.UserInfoTokenServices
import org.springframework.cloud.security.oauth2.sso.EnableOAuth2Sso
import org.springframework.cloud.security.oauth2.sso.OAuth2SsoConfigurer
import org.springframework.cloud.security.oauth2.sso.OAuth2SsoConfigurerAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices

@Configuration
@SpinnakerAuthConfig
// Use @EnableWebSecurity if/when updated to Spring Security 4.
@EnableWebMvcSecurity
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

  @Bean
  @ConditionalOnMissingBean(SpinnakerUserRolesProvider)
  SpinnakerUserRolesProvider defaultUserRolesProvider() {
    return new SpinnakerUserRolesProvider() {
      @Override
      Collection<String> loadRoles(String userEmail) {
        return []
      }
    }
  }

  /**
   * ResourceServerTokenServices is an interface used to manage access tokens. The UserInfoTokenService object is an
   * implementation of that interface that uses an access token to get the logged in user's data (such as email or
   * profile). We want to customize the Authentication object that is returned to include our custom (Kork) User.
   */
  @Primary
  @Bean
  ResourceServerTokenServices spinnakerAuthorityInjectedUserInfoTokenServices() {
    return new ResourceServerTokenServices() {

      @Autowired
      private ResourceServerProperties sso;

      @Autowired
      UserInfoTokenServices userInfoTokenServices

      @Autowired
      AnonymousAccountsService anonymousAccountsService

      @Autowired
      SpinnakerUserRolesProvider spinnakerUserRolesProvider

      @Override
      OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        OAuth2Authentication oAuth2Authentication = userInfoTokenServices.loadAuthentication(accessToken)

        // TODO(ttomsu): https://github.com/spring-projects/spring-boot/pull/5053 would obviate the need to create a
        // custom Authentication object just to override the Principal. Alas, it's not scheduled to be released until
        // Spring Boot 1.3.4.
        // See also https://github.com/spring-projects/spring-boot/commit/4768faaba771e35301b0ac68abf09cdb0e2f6881,
        // which adds an AuthoritiesExtractor, which eliminate the other need for this class (Authorities).
        SpinnakerAuthentication spinnakerAuthentication = new SpinnakerAuthentication(AuthorityUtils.createAuthorityList("IM_A_ROLE"))
        Map details = oAuth2Authentication.userAuthentication.details as Map
        spinnakerAuthentication.principal = new User(
          email: details.email,
          firstName: details.given_name,
          lastName: details.family_name,
          allowedAccounts: anonymousAccountsService.allowedAccounts,
          roles: spinnakerUserRolesProvider.loadRoles(details.email)
        )
        spinnakerAuthentication.setAuthenticated(true)

        // impl copied from userInfoTokenServices
        OAuth2Request storedRequest = new OAuth2Request(null, sso.clientId, null, true /*approved*/,
          null, null, null, null, null);

        return new OAuth2Authentication(storedRequest, spinnakerAuthentication)
      }

      @Override
      OAuth2AccessToken readAccessToken(String accessToken) {
        return userInfoTokenServices.readAccessToken(accessToken)
      }
    }
  }

  /**
   * Simple implementation to hold our Kork User as the Principal, which is used all over the framework.
   */
  static class SpinnakerAuthentication extends AbstractAuthenticationToken {
    def credentials
    User principal

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the
     *                    principal represented by this authentication object.
     */
    SpinnakerAuthentication(Collection<? extends GrantedAuthority> authorities) {
      super(authorities)
    }
  }
}
