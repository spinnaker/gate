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

package com.netflix.spinnaker.gate.security.saml

import com.netflix.spinnaker.gate.config.Headers
import com.netflix.spinnaker.gate.security.AnonymousAccountsService
import com.netflix.spinnaker.gate.security.anonymous.AnonymousConfig
import com.netflix.spinnaker.gate.services.internal.ClouddriverService
import com.netflix.spinnaker.security.User
import groovy.util.logging.Slf4j
import org.opensaml.saml2.binding.encoding.HTTPRedirectDeflateEncoder
import org.opensaml.saml2.core.Assertion
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.RememberMeServices

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.Response

@ConditionalOnExpression('${saml.enabled:false}')
@Slf4j
class SAMLLoginAuthenticator {
  private final String url
  private final String certificate
  private final SAMLConfig.SAMLSecurityConfigProperties samlSecurityConfigProperties
  private final ClouddriverService clouddriverService

  @Autowired
  SAMLLoginAuthenticator(SAMLConfig.SAMLSecurityConfigProperties properties, ClouddriverService clouddriverService) {
    this.url = properties.url
    this.certificate = properties.certificate
    this.samlSecurityConfigProperties = properties
    this.clouddriverService = clouddriverService
  }

  @Autowired
  RememberMeServices rememberMeServices

  @Autowired(required = false)
  AnonymousConfig anonymousSecurityConfig

  @Autowired
  AnonymousAccountsService anonymousAccountsService

  boolean handleAuthSignIn(HttpServletRequest request, HttpServletResponse response) {
    String samlResponse = request.getParameter("samlResponse")
    if (!samlResponse) {
      response.sendError(Response.Status.PRECONDITION_FAILED.statusCode, "Missing required samlResponse parameter.")
      return false
    }

    def assertion = SAMLUtils.buildAssertion(samlResponse,
                                             SAMLUtils.loadCertificate(samlSecurityConfigProperties.certificate))
    def user = buildUser(assertion,
                         samlSecurityConfigProperties.userAttributeMapping,
                         anonymousAccountsService.getAllowedAccounts(),
                         clouddriverService.getAccounts())
    if (!hasRequiredRole(anonymousSecurityConfig, samlSecurityConfigProperties, user)) {
      SecurityContextHolder.clearContext()
      rememberMeServices.loginFail(request, response)
      throw new BadCredentialsException("Credentials are bad")
    }
    def auth = new UsernamePasswordAuthenticationToken(user, "", [new SimpleGrantedAuthority("USER")])
    SecurityContextHolder.context.authentication = auth
    rememberMeServices.loginSuccess(request, response, auth)

    return true
  }

  void handleAuth(HttpServletRequest request, HttpServletResponse response) {
    URL redirect
    if (samlSecurityConfigProperties.redirectBase) {
      redirect = (samlSecurityConfigProperties.redirectBase + '/auth/signIn').toURI().normalize().toURL()
    } else {
      redirect = new URL(request.scheme, request.serverName, request.serverPort, request.contextPath + '/auth/signIn')
    }

    def authnRequest = SAMLUtils.buildAuthnRequest(url, redirect, samlSecurityConfigProperties.issuerId)
    def context = SAMLUtils.buildSAMLMessageContext(authnRequest, response, url)
    samlSecurityConfigProperties.with {
      def credential = SAMLUtils.buildCredential(keyStoreType, keyStore, keyStorePassword, keyStoreAliasName)
      if (credential.present) {
        context.setOutboundSAMLMessageSigningCredential(credential.get())
      }
    }

    new HTTPRedirectDeflateEncoder().encode(context)
  }

  static boolean hasRequiredRole(AnonymousConfig anonymousSecurityConfig,
                                 SAMLConfig.SAMLSecurityConfigProperties samlSecurityConfigProperties,
                                 User user) {
    if (samlSecurityConfigProperties.requiredRoles) {
      // ensure the user has at least one of the required roles (and at least one allowed account)
      return user.getRoles().find { String allowedRole ->
        samlSecurityConfigProperties.requiredRoles.contains(allowedRole)
      } && user.allowedAccounts
    }

    if (anonymousSecurityConfig && user.email == anonymousSecurityConfig.defaultEmail) {
      // force an anonymous user to login and get a proper set of roles/allowedAccounts
      return false
    }

    return user.allowedAccounts
  }

  User handleAuthInfo(HttpServletRequest request, HttpServletResponse response) {
    Object whoami = SecurityContextHolder.context.authentication.principal
    if (!whoami || !(whoami instanceof User) || !(hasRequiredRole(anonymousSecurityConfig, samlSecurityConfigProperties, whoami))) {
      response.addHeader Headers.AUTHENTICATION_REDIRECT_HEADER_NAME, "/auth"
      response.sendError 401
      return null
    }
    (User) whoami
  }

  static User buildUser(Assertion assertion,
                        SAMLConfig.UserAttributeMapping userAttributeMapping,
                        Collection<String> anonymousAllowedAccounts,
                        Collection<ClouddriverService.Account> allAccounts) {
    def attributes = SAMLUtils.extractAttributes(assertion)
    def roles = attributes[userAttributeMapping.roles].collect { String roles ->
      def commonNames = roles.split(";")
      commonNames.collect {
        return it.indexOf("CN=") < 0 ? it : it.substring(it.indexOf("CN=") + 3, it.indexOf(","))
      }
    }.flatten()*.toLowerCase()

    def allowedAccounts = (anonymousAllowedAccounts ?: []) as Set<String>
    allAccounts.findAll {
      it.requiredGroupMembership.find {
        roles.contains(it.toLowerCase())
      }
    }.each {
      allowedAccounts << it.name
    }

    def user = new User(
      assertion.getSubject().nameID.value,
      attributes[userAttributeMapping.firstName]?.get(0),
      attributes[userAttributeMapping.lastName]?.get(0),
      roles,
      allowedAccounts
    )

    return user
  }
}
