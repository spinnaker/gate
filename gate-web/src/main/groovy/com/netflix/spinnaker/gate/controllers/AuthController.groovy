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

package com.netflix.spinnaker.gate.controllers

import com.netflix.spinnaker.security.User
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.core.Response

@RequestMapping("/auth")
@RestController
@Slf4j
class AuthController {

  final String SPINNAKER_SSO_CALLBACK_KEY = "_SPINNAKER_SSO_CALLBACK"

  @Autowired(required = false)
  LoginAuthenticator loginAuthenticator

  @RequestMapping(method = RequestMethod.GET)
  void get(HttpServletRequest request, HttpServletResponse response) {
    if (!loginAuthenticator) {
      response.sendError(400)
      return
    }

    String cb = request.getParameter("callback")
    String hash = request.getParameter("path")

    def callback = cb && hash ? cb + '/#' + hash : cb
    request.session.setAttribute(SPINNAKER_SSO_CALLBACK_KEY, callback)

    loginAuthenticator.handleAuth(request, response)
  }

  @RequestMapping(value = "/info", method = RequestMethod.GET)
  User info(HttpServletRequest request, HttpServletResponse response) {
    if (!loginAuthenticator) {
      response.sendError(400)
      return
    }

    loginAuthenticator.handleAuthInfo(request, response)
  }

  @RequestMapping(value = "/signIn", method = [RequestMethod.GET, RequestMethod.POST])
  void signIn(HttpServletRequest request, HttpServletResponse response) {
    if (!loginAuthenticator) {
      response.sendError(400)
      return
    }

    def success = loginAuthenticator.handleAuthSignIn(request, response)

    if (success) {
      String callback = request.session.getAttribute(SPINNAKER_SSO_CALLBACK_KEY)
      if (!callback) {
        response.setStatus(Response.Status.OK.statusCode)
        return
      }
      response.sendRedirect callback
    }
  }

  /**
   * Implementing this interface indicates that a particular authentication mechanism is able to handle the auth dance
   * between Deck and Gate. There are 3 endpoints:
   *
   * 1.) /auth
   * 2.) /auth/info
   * 3.) /auth/signIn
   *
   * There should only be 1 implementation of this available at runtime
   *
   * @see com.netflix.spinnaker.gate.security.oauth2.client.OAuth2ClientConfig
   * @see com.netflix.spinnaker.gate.security.saml.SAMLConfig
   */
  static interface LoginAuthenticator {

    /**
     * Should return a redirect to the Identity Provider's login page
     */
    void handleAuth(HttpServletRequest request, HttpServletResponse response)

    /**
     * Returns the currently logged in {@link User}. Otherwise, returns a redirect to /auth
     */
    User handleAuthInfo(HttpServletRequest request, HttpServletResponse response)

    /**
     * The returning endpoint for after the user has authenticated with the Identity Provider. Normal processing of
     * the response may include:
     *
     * - extract User details (SAML)
     * - exchange authorization code for an access token (OAuth2)
     * - request and extract User Details using access token (OAuth2)
     */
    boolean handleAuthSignIn(HttpServletRequest request, HttpServletResponse response)
  }
}
