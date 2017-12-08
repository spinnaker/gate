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

package com.netflix.spinnaker.gate.security.oauth2

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spinnaker.gate.services.CredentialsService
import com.netflix.spinnaker.gate.services.PermissionService
import com.netflix.spinnaker.gate.services.internal.Front50Service
import com.netflix.spinnaker.security.User
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor
import org.springframework.boot.autoconfigure.security.oauth2.resource.FixedAuthoritiesExtractor
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.client.OAuth2RestOperations
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import retrofit.RetrofitError

import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

import static net.logstash.logback.argument.StructuredArguments.*

/**
 * ResourceServerTokenServices is an interface used to manage access tokens. The UserInfoTokenService object is an
 * implementation of that interface that uses an access token to get the logged in user's data (such as email or
 * profile). We want to customize the Authentication object that is returned to include our custom (Kork) User.
 */
@Slf4j
class SpinnakerUserInfoTokenServices implements ResourceServerTokenServices {
  @Autowired
  ResourceServerProperties sso

  @Autowired
  UserInfoTokenServices userInfoTokenServices

  @Autowired
  CredentialsService credentialsService

  @Autowired
  OAuth2SsoConfig.UserInfoMapping userInfoMapping

  @Autowired
  OAuth2SsoConfig.UserInfoRequirements userInfoRequirements

  @Autowired
  PermissionService permissionService

  @Autowired
  Front50Service front50Service

  String userInfoEndpointUrl
  String clientId

  private String tokenType = DefaultOAuth2AccessToken.BEARER_TYPE
  private OAuth2RestOperations restTemplate

  @Override
  OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
    Map<String, Object> map = getMap(this.userInfoEndpointUrl, accessToken)
    if (map.containsKey("error")) {
      log.debug("userinfo returned error: " + map.get("error"))
      throw new InvalidTokenException(accessToken)
    }
    OAuth2Authentication oAuth2Authentication = userInfoTokenServices.extractAuthentication(map)

    Map details = oAuth2Authentication.userAuthentication.details as Map

    if (log.isDebugEnabled()) {
      log.debug("UserInfo details: " + entries(details))
    }

    def isServiceAccount = isServiceAccount(details)
    if (!hasAllUserInfoRequirements(details) && !isServiceAccount) {
      throw new BadCredentialsException("User's info does not have all required fields.")
    }

    def username = details[userInfoMapping.username] as String
    def roles = []

    User spinnakerUser = new User(
        email: details[userInfoMapping.email] as String,
        firstName: details[userInfoMapping.firstName] as String,
        lastName: details[userInfoMapping.lastName] as String,
        allowedAccounts: credentialsService.getAccountNames(roles),
        roles: roles,
        username: username)

    PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(
        spinnakerUser,
        null /* credentials */,
        spinnakerUser.authorities
    )

    // Service accounts are already logged in.
    if (!isServiceAccount) {
      permissionService.login(username)
    }

    // impl copied from UserInfoTokenServices
    OAuth2Request storedRequest = new OAuth2Request(null, sso.clientId, null, true /*approved*/,
                                                    null, null, null, null, null);

    return new OAuth2Authentication(storedRequest, authentication)
  }

  private Map<String, Object> getMap(String path, String accessToken) {
    log.debug("Getting user info from: " + path);
    try {
      OAuth2RestOperations restTemplate = this.restTemplate;
      if (restTemplate == null) {
        BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
        resource.setClientId(this.clientId)
        restTemplate = new OAuth2RestTemplate(resource)
      }
      OAuth2AccessToken existingToken = restTemplate.getOAuth2ClientContext()
        .getAccessToken()
      if (existingToken == null || !accessToken.equals(existingToken.getValue())) {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(
          accessToken)
        token.setTokenType(this.tokenType)
        restTemplate.getOAuth2ClientContext().setAccessToken(token)
      }
      Map<String, Object> ret = restTemplate.getForEntity(path, Map.class).getBody()
      if (userInfoRequirements.containsKey("organization") && ret.containsKey("organizations_url")) {
        String org_url = ret["organizations_url"] as String
        log.debug("Getting user organizations from: " + org_url)
        List<Map<String, String>> organizations = restTemplate.getForEntity(org_url, List.class).getBody()
        ret["organizations"] = organizations
      }
      return ret
    }
    catch (Exception ex) {
      log.warn("Could not fetch user details: " + ex.getClass() + ", "
        + ex.getMessage())
      return Collections.<String, Object>singletonMap("error",
        "Could not fetch user details")
    }
  }

  @Override
  OAuth2AccessToken readAccessToken(String accessToken) {
    return userInfoTokenServices.readAccessToken(accessToken)
  }

  boolean isServiceAccount(Map details) {
    String email = details[userInfoMapping.serviceAccountEmail]
    if (!email || !permissionService.isEnabled()) {
      return false
    }
    try {
      def serviceAccounts = front50Service.getServiceAccounts()
      return serviceAccounts.find { email.equalsIgnoreCase(it.name) }
    } catch (RetrofitError re) {
      log.warn("Could not get list of service accounts.", re)
    }
    return false
  }

  boolean githubOrganizationMember(String organization, List<Map<String,String>> organizations) {
    for (int i = 0; i < organizations.size(); i++) {
      if (organization == organizations[i]["login"]) {
        return true
      }
    }
    return false
  }

  boolean hasAllUserInfoRequirements(Map details) {
    if (!userInfoRequirements) {
      return true
    }

    def invalidFields = userInfoRequirements.findAll { String reqKey, String reqVal ->
      if (reqKey == "organization") {
        if (details.containsKey("organizations")) {
          return !this.githubOrganizationMember(reqVal, details['organizations'])
        }
        return true
      }
      if (details[reqKey] && isRegexExpression(reqVal)) {
        return !String.valueOf(details[reqKey]).matches(mutateRegexPattern(reqVal))
      }
      return details[reqKey] != reqVal
    }
    if (invalidFields && log.debugEnabled) {
      String invalidSummary = invalidFields.collect({k, v ->
        if (k == "organization") {
          "missing required organization $v"
        } else {
          "got $k=${details[k]}, wanted $v"
        }}).join(", ")
      log.debug "Invalid userInfo response: " + invalidSummary
    }

    return !invalidFields
  }

  static boolean isRegexExpression(String val) {
    if (val.startsWith('/') && val.endsWith('/')) {
      try {
        Pattern.compile(val)
        return true
      } catch (PatternSyntaxException ignored) {
        return false
      }
    }
    return false
  }

  static String mutateRegexPattern(String val) {
    // "/expr/" -> "expr"
    val.substring(1, val.length() - 1)
  }
}
