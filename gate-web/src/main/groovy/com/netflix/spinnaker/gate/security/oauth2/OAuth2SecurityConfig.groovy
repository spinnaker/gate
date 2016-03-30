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

import com.netflix.discovery.converters.Auto
import com.netflix.spinnaker.gate.security.WebSecurityAugmentor
import com.netflix.spinnaker.security.AuthenticatedRequest
import com.netflix.spinnaker.security.User
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.client.OAuth2ClientContext
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.RestOperations
import org.springframework.web.client.RestTemplate

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Slf4j
@CompileStatic
@Configuration
@ConditionalOnExpression('${oauth2.enabled:false}')
class OAuth2SecurityConfig implements WebSecurityAugmentor {

  @Override
  void configure(HttpSecurity http, UserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
    def filter = new OAuth2AuthenticationProcessingFilter() {
      @Override
      void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (AuthenticatedRequest.getSpinnakerUser().isPresent()) {
          // No need to attempt OAuth if user is already authenticated
          chain.doFilter(req, res)
          return
        }
        super.doFilter(req, res, chain)
      }
    }

    filter.setAuthenticationManager(authenticationManager)
    http.addFilterBefore(filter, BasicAuthenticationFilter)

    http.csrf().disable()
    http.authorizeRequests()
      .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
      .antMatchers('/oauth/**').permitAll()
      .antMatchers('/health').permitAll()
      .antMatchers('/**').fullyAuthenticated()
  }

  @Override
  void configure(AuthenticationManagerBuilder authenticationManagerBuilder) {
    authenticationManagerBuilder.authenticationProvider(
      googleAuthenticationProvider(googleResourceServerTokenServices())
    )
  }

  // Google specific stuff
  @Bean
  AuthenticationProvider googleAuthenticationProvider(ResourceServerTokenServices resourceServerTokenServices) {
    return new GoogleOAuth2AuthenticationProvider(resourceServerTokenServices)
  }

  @Bean
  ResourceServerTokenServices googleResourceServerTokenServices() {
    new GoogleResourceServerTokenServices()
  }
  // End Google specific stuff

  // Netflix specific stuff
//  @Bean
//  AuthenticationProvider authenticationProvider(IdentityResourceServerTokenServices identityResourceServerTokenServices) {
//    return new OAuth2AuthenticationProvider(identityResourceServerTokenServices)
//  }
//
//  @Bean
//  IdentityResourceServerTokenServices identityResourceServerTokenServices(RestOperations restOperations) {
//    def defaultAccessTokenConverter = new DefaultAccessTokenConverter()
//    defaultAccessTokenConverter.userTokenConverter = new DefaultUserAuthenticationConverter()
//
//    return new IdentityResourceServerTokenServices(
//      identityServerConfiguration(), restOperations, defaultAccessTokenConverter
//    )
//  }
//
//  @Bean
//  @ConfigurationProperties('oauth2')
//  IdentityResourceServerTokenServices.IdentityServerConfiguration identityServerConfiguration() {
//    new IdentityResourceServerTokenServices.IdentityServerConfiguration()
//  }
//
//  static class DefaultUserAuthenticationConverter implements UserAuthenticationConverter {
//    @Override
//    Map<String, ?> convertUserAuthentication(Authentication userAuthentication) {
//      return [:]
//    }
//
//    @Override
//    Authentication extractAuthentication(Map<String, ?> map) {
//      def allowedAccounts = (map.scope ?: []).collect { String scope -> scope.replace("spinnaker_", "") }
//      def user = new User(map.client_id as String, null, null, [], allowedAccounts)
//      return new UsernamePasswordAuthenticationToken(user, "N/A", [])
//    }
//  }
  // End Netflix specific stuff

  @Bean
  @ConditionalOnMissingBean(RestTemplate)
  RestTemplate restTemplate() {
    def template = new RestTemplate()
    template.getMessageConverters().add(new FormHttpMessageConverter())
    return template
  }

  @Bean
  @ConfigurationProperties('spring.oauth2.client')
  protected OAuth2ProtectedResourceDetails resource() {
    new AuthorizationCodeResourceDetails()
  }
}
