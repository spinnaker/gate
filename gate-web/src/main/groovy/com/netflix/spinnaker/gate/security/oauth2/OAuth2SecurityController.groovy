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

import com.netflix.spinnaker.gate.config.Headers
import com.netflix.spinnaker.gate.security.AnonymousAccountsService
import com.netflix.spinnaker.gate.security.anonymous.AnonymousSecurityConfig
import com.netflix.spinnaker.gate.security.oauth2.OAuth2SecurityConfig.OAuth2Configuration
import com.netflix.spinnaker.security.User
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.UriBuilder
import java.nio.file.AccessDeniedException

@RequestMapping(value = "/oauth")
@RestController
@Slf4j
@ConditionalOnExpression('${oauth2.enabled:false}')
class OAuth2SecurityController {

  @Autowired
  RestTemplate restTemplate

  @Autowired
  OAuth2Configuration oAuth2Configuration

  @Autowired(required = false)
  AnonymousSecurityConfig anonymousSecurityConfig

  @Autowired
  AnonymousAccountsService anonymousAccountsService

  @Value('${services.deck.baseUrl:http://localhost:9000}')
  String deckUrl

  @RequestMapping(value = "/code")
  void codeCallback(HttpServletRequest request, HttpServletResponse response,
                    @RequestParam(value = "code", required = false) String code,
                    @RequestParam(value = "error", required = false) String error) {
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
    form.add('redirect_uri', request.scheme + '://' + request.serverName + ':' + request.serverPort + '/oauth/code')
    form.add('grant_type', 'authorization_code')
    ResponseEntity<DefaultOAuth2AccessToken> tokenResponse = restTemplate.postForEntity(oAuth2Configuration.accessTokenUri, form, DefaultOAuth2AccessToken)

    // get email
    HttpHeaders headers = new HttpHeaders()
    headers.set('Authorization', 'Bearer ' + tokenResponse.body.value)
    def infoReq = new HttpEntity(headers)
    ResponseEntity<Map> userInfo = restTemplate.exchange(oAuth2Configuration.userInfoUri, HttpMethod.GET, infoReq, Map)
    log.info(userInfo.body.toString())

    // populate security context
    def user = new User(userInfo.body.email, null, null, ["user"], anonymousAccountsService.getAllowedAccounts())
    // TODO(jacobkiefer): service accounts?
    PreAuthenticatedAuthenticationToken authn = new PreAuthenticatedAuthenticationToken(user, null)
    authn.setAuthenticated(true)
    SecurityContextHolder.context.setAuthentication(authn)

    response.sendRedirect(deckUrl)
  }

  @RequestMapping(value = "/info", method = RequestMethod.GET)
  User getUser(HttpServletRequest request, HttpServletResponse response) {
    Object whoami = SecurityContextHolder.context.authentication.principal
    if (!whoami || !(whoami instanceof User) || !(hasRequiredRole(anonymousSecurityConfig, oAuth2Configuration, whoami))) {

      UriBuilder redirectBuilder = UriBuilder.fromUri(oAuth2Configuration.userAuthorizationUri)
      // TODO(jacobkiefer): add in state qparam?
      redirectBuilder.queryParam('response_type', 'code')
      redirectBuilder.queryParam('client_id', oAuth2Configuration.clientId)
      redirectBuilder.queryParam('redirect_uri',
        URLEncoder.encode(request.scheme + '://' + request.serverName + ':' + request.serverPort + request.contextPath + '/oauth/code', 'UTF-8'))
      redirectBuilder.queryParam('scope', 'profile email')

      response.addHeader Headers.AUTHENTICATION_REDIRECT_HEADER_NAME, redirectBuilder.build().toString()
      response.sendError 401
      null
    } else {
      (User) whoami
    }
  }

  static boolean hasRequiredRole(AnonymousSecurityConfig anonymousSecurityConfig,
                                 OAuth2Configuration oAuth2Configuration,
                                 User user) {
    // TODO(jacobkiefer): requiredRoles
    if (anonymousSecurityConfig && user.email == anonymousSecurityConfig.defaultEmail) {
      // force an anonymous user to login and get a proper set of roles/allowedAccounts
      return false
    }

    return true
  }

}
