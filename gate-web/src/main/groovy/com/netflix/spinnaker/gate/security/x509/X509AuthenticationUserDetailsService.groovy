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

package com.netflix.spinnaker.gate.security.x509

import com.netflix.spinnaker.gate.security.AnonymousAccountsService
import com.netflix.spinnaker.gate.security.SpinnakerUserDetails
import com.netflix.spinnaker.security.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Component

import java.security.cert.X509Certificate

/**
 * This class is similar to a UserDetailService, but instead of passing in a username to loadUserDetails,
 * it passess in a token containing the x509 certificate. A user can control the principal through the
 * `spring.x509.subjectPrincipalRegex` property.
 */
@Component
class X509AuthenticationUserDetailsService implements AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

  @Autowired
  AnonymousAccountsService anonymousAccountsService

  @Override
  UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken token) throws UsernameNotFoundException {
    if (!(token.credentials instanceof X509Certificate)) {
      return null
    }

    return new SpinnakerUserDetails(
        spinnakerUser: new User(email: token.principal,
                                allowedAccounts: anonymousAccountsService.allowedAccounts))
  }
}
