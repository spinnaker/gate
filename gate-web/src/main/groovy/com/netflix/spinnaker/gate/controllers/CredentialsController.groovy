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

import com.netflix.spinnaker.gate.security.SpinnakerUser
import com.netflix.spinnaker.gate.services.CredentialsService
import com.netflix.spinnaker.gate.services.internal.ClouddriverService
import com.netflix.spinnaker.security.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.prepost.PostFilter
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/credentials")
class CredentialsController {

  @Autowired
  CredentialsService credentialsService

  @PostFilter("hasPermission(filterObject.name, 'ACCOUNT', 'READ')")
  @RequestMapping(method = RequestMethod.GET)
  List<ClouddriverService.Account> getAccounts(@SpinnakerUser User user) {
    credentialsService.getAccounts(user.roles)
  }

  @PreAuthorize("hasPermission(#account, 'ACCOUNT', 'READ')")
  @RequestMapping(value = '/{account}', method = RequestMethod.GET)
  Map getAccount(@PathVariable("account") String account) {
    credentialsService.getAccount(account)
  }
}
