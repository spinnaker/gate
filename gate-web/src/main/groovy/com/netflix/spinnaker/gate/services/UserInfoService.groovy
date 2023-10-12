/*
 * Copyright 2023 Netflix, Inc.
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

package com.netflix.spinnaker.gate.services

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.netflix.spinnaker.security.User
import com.opsmx.spinnaker.gate.model.UserInfoDetailsModel
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Slf4j
@Service
class UserInfoService {

  Gson gson = new Gson()

  Object getAllInfoOfUser(User user, Object response) throws Exception {

    UserInfoDetailsModel userInfoDetails = new UserInfoDetailsModel()
    try {
      log.info("CloudProviderAccounts response from oes service: {}", response)
      def inputStr = gson.toJson(response)
      def extractedCloudAccounts = JsonParser.parseString(inputStr).getAsJsonArray()

      def cloudAccounts = extractedCloudAccounts.collect { accountJson ->
        def accountType = accountJson.getAsJsonPrimitive("accountType").getAsString()
        def name = accountJson.getAsJsonPrimitive("name").getAsString()
        def cloudAccount = [cloudProvider: accountType, accountName: name]
        cloudAccount
      }
      log.info("Extracted cloudAccounts for user: {}", cloudAccounts)

      userInfoDetails.cloudAccounts = cloudAccounts
      userInfoDetails.userName = user.username
      userInfoDetails.firstName = user.firstName
      userInfoDetails.lastName = user.lastName
      userInfoDetails.userMailId = user.email
      userInfoDetails.userRoles = user.roles

    } catch (Exception e) {
      e.printStackTrace()
    }
    return userInfoDetails
  }
}
