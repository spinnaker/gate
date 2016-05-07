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
import com.netflix.spinnaker.gate.security.SpinnakerAuthConfig
import com.netflix.spinnaker.gate.services.AnonymousAccountsService
import com.netflix.spinnaker.security.User
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.web.ServerProperties
import org.springframework.boot.context.embedded.Ssl
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.saml.SAMLCredential
import org.springframework.security.saml.userdetails.SAMLUserDetailsService
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices
import org.springframework.stereotype.Component

import static org.springframework.security.extensions.saml2.config.SAMLConfigurer.saml

@ConditionalOnExpression('${saml.enabled:false}')
@Configuration
@SpinnakerAuthConfig
@EnableWebMvcSecurity
@Import(SecurityAutoConfiguration)
@Slf4j
class SamlSsoConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  ServerProperties serverProperties

  @Component
  @ConfigurationProperties("saml")
  static class SAMLSecurityConfigProperties {
    String keyStore
    String keyStorePassword
    String keyStoreAliasName

    // SAML DSL uses a metadata URL instead of hard coding a certificate/issuerId/redirectBase into the config.
    String metadataUrl
    // The hostname of this server passed to the SAML IdP.
    String redirectHostname

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

  @Autowired
  SAMLUserDetailsService samlUserDetailsService

  @Override
  void configure(HttpSecurity http) {
    http.rememberMe().rememberMeServices(rememberMeServices(userDetailsService()))

    if (samlSecurityConfigProperties.requireAuthentication) {
      http.authorizeRequests().antMatchers("/saml/**").permitAll()
      AuthConfig.configure(http)
    }

    http.apply(
        saml().keyStore()
            .storeFilePath(samlSecurityConfigProperties.keyStore)
            .password(samlSecurityConfigProperties.keyStorePassword)
            .keyname(samlSecurityConfigProperties.keyStoreAliasName)
            .keyPassword(samlSecurityConfigProperties.keyStorePassword)
            .and()
            .entityId(samlSecurityConfigProperties.issuerId)
            .protocol(serverProperties?.ssl?.enabled ? "https" : "http")
            .hostname(samlSecurityConfigProperties.redirectHostname ?: serverProperties?.address?.hostName)
            .basePath("/")
            .metadataFilePath(samlSecurityConfigProperties.metadataUrl)
            .userDetailsService(samlUserDetailsService))
  }

  @Bean
  public RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
    TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices("password", userDetailsService)
    rememberMeServices.setCookieName("cookieName")
    rememberMeServices.setParameter("rememberMe")
    rememberMeServices
  }

  @Bean
  SAMLUserDetailsService samlUserDetailsService() {
    new SAMLUserDetailsService() {

      @Autowired
      AnonymousAccountsService anonymousAccountsService

      @Override
      User loadUserBySAML(SAMLCredential credential) throws UsernameNotFoundException {
        new User(email: credential?.nameID?.value,
                 roles: [],
                 allowedAccounts: anonymousAccountsService.allowedAccounts)
      }
    }
  }
}
