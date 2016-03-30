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

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.security.oauth2.provider.AuthorizationRequest
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.web.client.RestTemplate

class GoogleResourceServerTokenServices implements ResourceServerTokenServices {

  @Autowired
  RestTemplate restTemplate

  @Autowired
  OAuth2ProtectedResourceDetails oAuth2ProtectedResourceDetails // OAuth2 config details

  @Override
  OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
    def uri = 'https://www.googleapis.com/oauth2/v3/tokeninfo' + '?access_token=' + URLEncoder.encode(accessToken, 'UTF-8') // TODO(jacobkiefer): hardcoded
    Map<String, ?> responseMap = restTemplate.getForObject(uri, Map)
    if (responseMap?.error || (responseMap.aud != oAuth2ProtectedResourceDetails.clientId)) {
      return null
    }
    def reqFactory = new DefaultOAuth2RequestFactory()
    def authzReq = new AuthorizationRequest(oAuth2ProtectedResourceDetails.clientId, oAuth2ProtectedResourceDetails.scope)
    authzReq.setApproved(true)
    OAuth2Request oauthzReq = reqFactory.createOAuth2Request(authzReq)
    def authn = new OAuth2Authentication(oauthzReq, null)
    authn.setAuthenticated(true)
    return authn
  }

  @Override
  OAuth2AccessToken readAccessToken(String accessToken) {
    throw new UnsupportedOperationException("Not supported: read access token") // TODO(jacobkiefer): is this true for Google oauth2 provider
  }
}
