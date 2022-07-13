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
import com.netflix.spinnaker.gate.security.AllowedAccountsSupport
import com.netflix.spinnaker.gate.security.SpinnakerUser
import com.netflix.spinnaker.gate.services.AccountLookupService
import com.netflix.spinnaker.gate.services.internal.ClouddriverService
import com.netflix.spinnaker.gate.services.internal.ClouddriverService.Account
import com.netflix.spinnaker.gate.services.internal.ClouddriverService.AccountDetails
import com.netflix.spinnaker.kork.annotations.Alpha
import com.netflix.spinnaker.security.User
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/credentials")
class CredentialsController {

  @Autowired
  AccountLookupService accountLookupService

  @Autowired
  AllowedAccountsSupport allowedAccountsSupport

  @Autowired
  ClouddriverService clouddriverService

  @Autowired
  ObjectMapper objectMapper

  static class AccountWithAuthorization extends Account {
    Boolean authorized
  }

  @ApiOperation(value = "Retrieve a list of accounts")
  @RequestMapping(method = RequestMethod.GET)
  List<Account> getAccounts(@SpinnakerUser User user, @RequestParam(value = "expand", required = false) boolean expand) {
    List<AccountDetails> allAccounts = getAccountDetailsWithAuthorizedFlag(user)
    if (expand) {
      return allAccounts
    }
    return objectMapper.convertValue(allAccounts, new TypeReference<List<CredentialsController.AccountWithAuthorization>>() {})
  }

  private List<AccountDetails> getAccountDetailsWithAuthorizedFlag(User user) {
    List<AccountDetails> allAccounts = accountLookupService.getAccounts()
    Collection<String> allowedAccounts = user == null ?
      Collections.emptySet() :
      allowedAccountsSupport.filterAllowedAccounts(user.username, user.roles)

    for (AccountDetails account : allAccounts) {
      account.set('authorized', allowedAccounts.contains(account.name) ? Boolean.TRUE : Boolean.FALSE)
    }
    return allAccounts
  }

  @ApiOperation(value = "Retrieve an account's details")
  @RequestMapping(value = '/{account:.+}', method = RequestMethod.GET)
  AccountDetails getAccount(@SpinnakerUser User user, @PathVariable("account") String account,
                            @RequestHeader(value = "X-RateLimit-App", required = false) String sourceApp) {
    return getAccountDetailsWithAuthorizedFlag(user).find { it.name == account }
  }

  @GetMapping('/type/{accountType}')
  @ApiOperation('Looks up account definitions by type.')
  @Alpha
  List<ClouddriverService.AccountDefinition> getAccountsByType(
    @ApiParam(value = 'Value of the "@type" key for accounts to search for.', example = 'kubernetes')
    @PathVariable String accountType,
    @ApiParam('Maximum number of entries to return in results. Used for pagination.')
    @RequestParam OptionalInt limit,
    @ApiParam('Account name to start account definition listing from. Used for pagination.')
    @RequestParam Optional<String> startingAccountName
  ) {
    clouddriverService.getAccountDefinitionsByType(accountType, limit.isPresent() ? limit.getAsInt() : null, startingAccountName.orElse(null))
  }

  @PostMapping
  @ApiOperation('Creates a new account definition.')
  @Alpha
  ClouddriverService.AccountDefinition createAccount(
    @ApiParam('Account definition body including a discriminator field named "type" with the account type.')
    @RequestBody ClouddriverService.AccountDefinition accountDefinition
  ) {
    clouddriverService.createAccountDefinition(accountDefinition)
  }

  @PutMapping
  @ApiOperation('Updates an existing account definition.')
  @Alpha
  ClouddriverService.AccountDefinition updateAccount(
    @ApiParam('Account definition body including a discriminator field named "type" with the account type.')
    @RequestBody ClouddriverService.AccountDefinition accountDefinition
  ) {
    clouddriverService.updateAccountDefinition(accountDefinition)
  }

  @DeleteMapping('/{accountName}')
  @ApiOperation('Deletes an account definition by name.')
  @Alpha
  void deleteAccount(
    @ApiParam('Name of account definition to delete.')
    @PathVariable String accountName
  ) {
    clouddriverService.deleteAccountDefinition(accountName)
  }
}
