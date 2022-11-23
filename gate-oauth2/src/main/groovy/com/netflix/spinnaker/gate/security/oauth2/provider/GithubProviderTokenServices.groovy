/*
 * Copyright 2017 Armory, Inc.
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

package com.netflix.spinnaker.gate.security.oauth2.provider

import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.security.oauth2.client.OAuth2RestOperations
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails
import org.springframework.stereotype.Component
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpHeaders

@Slf4j
@Component
@ConditionalOnExpression(''''${security.oauth2.provider-requirements.type:}' == "github"''')
class GithubProviderTokenServices implements SpinnakerProviderTokenServices {
  @Autowired
  ResourceServerProperties sso

  @Autowired
  GithubRequirements requirements

  private String tokenType = DefaultOAuth2AccessToken.BEARER_TYPE
  OAuth2RestOperations restTemplate

  @Component
  @ConfigurationProperties("security.oauth2.provider-requirements")
  static class GithubRequirements {
    String organization
  }

  boolean githubOrganizationMember(String organization, List<Map<String,String>> organizations) {
    for (int i = 0; i < organizations.size(); i++) {
      if (organization == organizations[i]["login"]) {
        return true
      }
    }
    return false
  }

  boolean checkOrganization(String accessToken, String organizationsUrl, String organization) {
    try {
      log.debug("Getting user organizations from URL {}", organizationsUrl)
      OAuth2RestOperations restTemplate = this.restTemplate
      if (restTemplate == null) {
        BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails()
        resource.setClientId(sso.clientId)
        restTemplate = new OAuth2RestTemplate(resource)
      }
      OAuth2AccessToken existingToken = restTemplate.getOAuth2ClientContext().getAccessToken()
      if (existingToken == null || !accessToken.equals(existingToken.getValue())) {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(accessToken)
        token.setTokenType(this.tokenType)
        restTemplate.getOAuth2ClientContext().setAccessToken(token)
      }
      ResponseEntity<List<Map<String, String>>> response = restTemplate.getForEntity(organizationsUrl, List.class);
      HttpHeaders headers = response.getHeaders();
      boolean isMember = githubOrganizationMember(organization, response.getBody())
      while (!isMember && hasNextPage(headers)) {
        log.debug('Checking next page of user organizations')
        response = restTemplate.getForEntity(nextPageUrl(nextLink(headers)), List.class)
        isMember = githubOrganizationMember(organization, response.getBody())
        headers = response.getHeaders();
      }
      return isMember
    }
    catch (Exception e) {
      log.warn("Could not fetch user organizations", e)
      return Collections.<String, Object>singletonMap("error", "Could not fetch user organizations")
    }
  }

  boolean hasAllProviderRequirements(String token, Map details) {
    boolean hasRequirements = true
    if (requirements.organization != null && details.containsKey("organizations_url")) {
      boolean orgMatch = checkOrganization(token, details['organizations_url'], requirements.organization)
      if (!orgMatch) {
        log.debug("User does not include required organization {}", requirements.organization)
        hasRequirements = false
      }
    }
    return hasRequirements
  }

  private boolean hasNextPage(HttpHeaders headers) {
    return headers.containsKey('link') ? nextLink(headers) != null : false
  }

  private String nextPageUrl(String nextLink) {
    def urlPart = nextLink.split(';')[0]
    return urlPart.substring(1, urlPart.length() - 1)
  }

  private String nextLink(HttpHeaders headers) {
    String[] links = headers.getFirst('link').split(',')
    return links.find { it.contains('rel="next"') }
  }
}
