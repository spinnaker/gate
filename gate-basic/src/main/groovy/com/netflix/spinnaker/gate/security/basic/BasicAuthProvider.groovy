/*
 * Copyright 2017 Google, Inc.
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
package com.netflix.spinnaker.gate.security.basic

import com.netflix.spinnaker.security.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component


@ConditionalOnExpression('${security.basic.enabled:false}')
@Component
class BasicAuthProvider implements AuthenticationProvider {

  @Autowired
  SecurityProperties securityProperties

  @Override
  Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String name = authentication.getName()
    String password = authentication.getCredentials()?.toString()

    if (securityProperties.user == null) {
      throw new AuthenticationServiceException("User credentials are not configured for the service")
    }

    if (name != securityProperties.user.name || password != securityProperties.user.password) {
      throw new BadCredentialsException("Invalid username/password combination")
    }

    def user = new User()
    user.email = name
    user.username = name
    user.roles = Collections.singletonList("USER")
    return new UsernamePasswordAuthenticationToken(user, password, new ArrayList<>())
  }

  @Override
  boolean supports(Class<?> authentication) {
    return authentication == UsernamePasswordAuthenticationToken.class
  }
}
