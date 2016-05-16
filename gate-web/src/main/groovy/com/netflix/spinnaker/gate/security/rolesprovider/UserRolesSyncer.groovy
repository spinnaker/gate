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

package com.netflix.spinnaker.gate.security.rolesprovider

import com.netflix.spinnaker.security.User
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.context.SecurityContext
import org.springframework.session.ExpiringSession

@Slf4j
@Configuration
class UserRolesSyncer {

  @Autowired
  RedisTemplate<String, ExpiringSession> redisTemplate

  @Autowired
  UserRolesProvider userRolesProvider

  /**
   Check if a user's groups have changed across _ALL_ sessions every 10 minutes, after an initial delay.
   Invalidate the user's session if his groups have changed.
   */
  @Scheduled(initialDelay = 10000L, fixedRate = 600000L)
  public void syncUserGroups() {
    log.info("Syncing user groups in sessions")
    Set<String> sessionKeys = redisTemplate.keys('*session:sessions*')
    sessionKeys.each { String key ->
      println key
      def secCtx = redisTemplate.opsForHash().get(key, "sessionAttr:SPRING_SECURITY_CONTEXT") as SecurityContext
      println secCtx.toString()
      def principal = secCtx?.authentication?.principal
      if (principal && principal instanceof User) {
        def newRoles = userRolesProvider.loadRoles(principal.email)
        if (newRoles != principal.roles) {
          redisTemplate.delete(key)
        }
      }
    }
  }

}
