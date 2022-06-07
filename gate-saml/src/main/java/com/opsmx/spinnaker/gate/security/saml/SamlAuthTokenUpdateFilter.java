/*
 * Copyright 2022 OpsMx, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package com.opsmx.spinnaker.gate.security.saml;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.providers.ExpiringUsernameAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;

public class SamlAuthTokenUpdateFilter extends GenericFilterBean {

  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {
    logger.debug("SamlAuthTokenUpdateFilter doFilter started");
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    logger.debug("Previously Authenticated is : " + authentication);
    if (authentication instanceof ExpiringUsernameAuthenticationToken
        && !authentication.isAuthenticated()) {
      if (logger.isDebugEnabled()) {
        logger.debug(
            "Previously Authenticated token Expired; redirecting to authentication entry point.");
      }

      HttpSession session = request.getSession();
      if (session != null) {
        session.invalidate();
      }
      throw new SAMLAuthenticationException("Previously Authenticated token Expired");
    }
    logger.debug("SamlAuthTokenUpdateFilter doFilter ended");

    chain.doFilter(request, response);
  }
}
