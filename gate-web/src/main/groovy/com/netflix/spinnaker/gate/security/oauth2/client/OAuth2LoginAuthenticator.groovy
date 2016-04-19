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

package com.netflix.spinnaker.gate.security.oauth2.client

import com.netflix.spinnaker.gate.config.Headers
import com.netflix.spinnaker.gate.security.AnonymousAccountsService
import com.netflix.spinnaker.gate.controllers.AuthController
import com.netflix.spinnaker.gate.security.anonymous.AnonymousConfig
import com.netflix.spinnaker.security.User
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.UriBuilder
import java.nio.file.AccessDeniedException

@Slf4j
@ConditionalOnExpression('${auth.oauth2Client.enabled:false}')
@Component
class OAuth2LoginAuthenticator implements AuthController.LoginAuthenticator {

  @Autowired
  RestTemplate restTemplate

  @Autowired
  OAuth2ClientConfig oAuth2Configuration

  @Autowired(required = false)
  AnonymousConfig anonymousSecurityConfig

  @Autowired
  AnonymousAccountsService anonymousAccountsService

  @Override
  boolean handleAuthSignIn(HttpServletRequest request, HttpServletResponse response) {
    String code = request.getParameter("code")
    String error = request.getParameter("error")

    if (!error && !code) {
      throw new OAuth2Exception("Neither code nor error returned in OAuth2 code call")
    }

    if (error) {
      throw new AccessDeniedException("OAuth2 access denied")
    }

    // get token
    def form = new LinkedMultiValueMap<String, String>();
    form.add('code', code)
    form.add('client_id', oAuth2Configuration.clientId)
    form.add('client_secret', oAuth2Configuration.clientSecret)
    form.add('redirect_uri', request.scheme + '://' + request.serverName + ':' + request.serverPort + '/auth/signIn')
    form.add('grant_type', 'authorization_code')
    HttpHeaders headers = new HttpHeaders()
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED)
    HttpEntity entity = new HttpEntity(form, headers)
    DefaultOAuth2AccessToken tokenResponse = restTemplate.postForObject(oAuth2Configuration.accessTokenUri,
                                                                        entity,
                                                                        DefaultOAuth2AccessToken)

    // get email
    headers = new HttpHeaders()
    headers.set('Authorization', 'Bearer ' + tokenResponse.value)
    def infoReq = new HttpEntity(headers)
    ResponseEntity<Map> userInfo = restTemplate.exchange(oAuth2Configuration.userInfoUri, HttpMethod.GET, infoReq, Map)
    Map userData = userInfo.body
    log.info(userData.toString())

    // populate security context
    def user = new User(email: userData.email,
                        firstName: userData.given_name,
                        lastName: userData.family_name,
                        roles: ["user"],
                        allowedAccounts: anonymousAccountsService.getAllowedAccounts())
    // TODO(jacobkiefer): service accounts?
    PreAuthenticatedAuthenticationToken authn = new PreAuthenticatedAuthenticationToken(user, null)
    authn.setAuthenticated(true)
    SecurityContextHolder.context.setAuthentication(authn)

    return true
  }

  @Override
  void handleAuth(HttpServletRequest request, HttpServletResponse response) {
    UriBuilder redirectBuilder = UriBuilder.fromUri(oAuth2Configuration.userAuthorizationUri)
    // TODO(jacobkiefer): add in state qparam?
    redirectBuilder.queryParam('response_type', 'code')
    redirectBuilder.queryParam('client_id', oAuth2Configuration.clientId)
    redirectBuilder.queryParam('scope', oAuth2Configuration.scope.join(" "))
    redirectBuilder.queryParam('redirect_uri',
                               URLEncoder.encode(request.scheme + '://' + request.serverName + ':' + request.serverPort + request.contextPath + '/auth/signIn', 'UTF-8'))


    response.sendRedirect(redirectBuilder.build().toString())
  }

  @Override
  User handleAuthInfo(HttpServletRequest request, HttpServletResponse response) {
    Object whoami = SecurityContextHolder.context.authentication.principal
    if (whoami && (whoami instanceof User) && (hasRequiredRole(anonymousSecurityConfig, whoami))) {
      return (User) whoami
    }

    response.addHeader Headers.AUTHENTICATION_REDIRECT_HEADER_NAME, "/auth"
    response.sendError 401
  }

  static boolean hasRequiredRole(AnonymousConfig anonymousSecurityConfig,
                                 User user) {
    // TODO(jacobkiefer): requiredRoles
    if (anonymousSecurityConfig && user.email == anonymousSecurityConfig.defaultEmail) {
      // force an anonymous user to login and get a proper set of roles/allowedAccounts
      return false
    }

    return true
  }

}
