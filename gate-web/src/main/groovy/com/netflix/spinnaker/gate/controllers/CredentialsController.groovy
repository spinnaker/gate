/*
 * Copyright 2014 Netflix, Inc.
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

package com.netflix.spinnaker.gate.controllers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spinnaker.gate.security.SpinnakerUser
import com.netflix.spinnaker.gate.services.CredentialsService
import com.netflix.spinnaker.gate.services.internal.ClouddriverService.Account
import com.netflix.spinnaker.gate.services.internal.ClouddriverService.AccountDetails
import com.netflix.spinnaker.security.User
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/credentials")
class CredentialsController {

  @Autowired
  CredentialsService credentialsService

  @Autowired
  ObjectMapper objectMapper

  @PreAuthorize("@fiatPermissionEvaluator.storeWholePermission()")
  @PostFilter("hasPermission(filterObject.name, 'ACCOUNT', 'READ')")
  @ApiOperation(value = "Retrieve a list of accounts")
  @RequestMapping(method = RequestMethod.GET)
  List<Account> getAccounts(@SpinnakerUser User user, @RequestParam(value = "expand", required = false) boolean expand) {
    List<AccountDetails> allAccounts = credentialsService.getAccounts(user?.roles ?: [])
    if (expand) {
      return allAccounts
    }
    return objectMapper.convertValue(allAccounts, new TypeReference<List<Account>>() {})
  }

  @PreAuthorize("hasPermission(#account, 'ACCOUNT', 'READ')")
  @ApiOperation(value = "Retrieve an account's details")
  @RequestMapping(value = '/{account:.+}', method = RequestMethod.GET)
  AccountDetails getAccount(@PathVariable("account") String account,
                            @RequestHeader(value = "X-RateLimit-App", required = false) String sourceApp) {
    credentialsService.getAccount(account, sourceApp)
  }
}
