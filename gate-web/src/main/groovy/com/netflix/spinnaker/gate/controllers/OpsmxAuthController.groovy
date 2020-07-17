/*
 * Copyright 2020 Netflix, Inc.
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

package com.netflix.spinnaker.gate.controllers

import groovy.util.logging.Slf4j
import io.swagger.annotations.ApiOperation
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import javax.servlet.http.HttpServletResponse

@Slf4j
@RestController
@RequestMapping("/auth")
class OpsmxAuthController {

  @ApiOperation(value = "Redirect to Deck")
  @RequestMapping(value = "/redirectauto", method = RequestMethod.GET)
  void redirectAuto(HttpServletResponse response, @RequestParam String to) {

    validAutoRedirect(to) ?
      response.sendRedirect(to) :
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Requested redirect address not recognized.")
  }

  boolean validAutoRedirect(String to) {
    URL toURL
    try {
      toURL = new URL(to)
    } catch (MalformedURLException malEx) {
      log.warn "Malformed redirect URL: $to\n${ExceptionUtils.getStackTrace(malEx)}"
      return false
    }

    log.debug([
      "validateDeckRedirect(${to})",
      "toUrl(host: ${toURL.host}, port: ${toURL.port})",
      "deckBaseUrl(host: ${deckBaseUrl.host}, port: ${deckBaseUrl.port})",
      "redirectHostPattern(${redirectHostPattern?.pattern()})"
    ].join(" - ")
    )
    return true
  }
}
