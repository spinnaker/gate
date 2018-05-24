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

package com.netflix.spinnaker.gate.services

import com.netflix.spinnaker.fiat.model.UserPermission
import com.netflix.spinnaker.fiat.model.resources.Role
import com.netflix.spinnaker.fiat.shared.FiatClientConfigurationProperties
import com.netflix.spinnaker.fiat.shared.FiatPermissionEvaluator
import com.netflix.spinnaker.fiat.shared.FiatService
import com.netflix.spinnaker.gate.security.SpinnakerUser
import com.netflix.spinnaker.gate.services.commands.HystrixFactory
import com.netflix.spinnaker.security.User
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import retrofit.RetrofitError

import static com.netflix.spinnaker.gate.retrofit.UpstreamBadRequest.classifyError

@Slf4j
@Component
class PermissionService {

  static final String HYSTRIX_GROUP = "permission"

  @Autowired
  FiatService fiatService

  @Autowired
  FiatClientConfigurationProperties fiatConfig

  @Autowired
  FiatPermissionEvaluator permissionEvaluator

  boolean isEnabled() {
    return fiatConfig.enabled
  }

  void login(String userId) {
    if (fiatConfig.enabled) {
      HystrixFactory.newVoidCommand(HYSTRIX_GROUP, "login") {
        try {
          fiatService.loginUser(userId, "")
        } catch (RetrofitError e) {
          classifyError(e)
        }
      }.execute()
    }
  }

  void loginWithRoles(String userId, Collection<String> roles) {
    if (fiatConfig.enabled) {
      HystrixFactory.newVoidCommand(HYSTRIX_GROUP, "loginWithRoles") {
        try {
          fiatService.loginWithRoles(userId, roles)
        } catch (RetrofitError e) {
          classifyError(e)
        }
      }.execute()
    }
  }

  void logout(String userId) {
    if (fiatConfig.enabled) {
      HystrixFactory.newVoidCommand(HYSTRIX_GROUP, "logout") {
        try {
          fiatService.logoutUser(userId)
        } catch (RetrofitError e) {
          classifyError(e)
        }
      }.execute()
    }
  }

  void sync() {
    if (fiatConfig.enabled) {
      HystrixFactory.newVoidCommand(HYSTRIX_GROUP, "sync") {
        try {
          fiatService.sync()
        } catch (RetrofitError e) {
          classifyError(e)
        }
      }.execute()
    }
  }

  Set<Role> getRoles(String userId) {
    if (!fiatConfig.enabled) {
      return []
    }
    return HystrixFactory.newListCommand(HYSTRIX_GROUP, "getRoles") {
      try {
        return permissionEvaluator.getPermission(userId)?.roles ?: []
      } catch (RetrofitError e) {
        classifyError(e)
      }
    }.execute() as Set<Role>
  }

  List<String> getServiceAccounts(@SpinnakerUser User user) {

    if (!user) {
      log.debug("getServiceAccounts: Spinnaker user is null.")
      return []
    }

    if (!fiatConfig.enabled) {
      log.debug("getServiceAccounts: Fiat disabled.")
      return []
    }

    return HystrixFactory.newListCommand(HYSTRIX_GROUP, "getServiceAccounts") {
      try {
        UserPermission.View view = permissionEvaluator.getPermission(user.username)
        return view.getServiceAccounts().collect { it.name }
      } catch (RetrofitError re) {
        throw classifyError(re)
      }
    }.execute() as List<String>
  }
}
