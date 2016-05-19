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
   * Check all sessions to see whether the session user's groups have changed. If so, delete the session.
   * Repeat every 10 minutes, after an initial delay.
   */
  @Scheduled(initialDelay = 10000L, fixedRate = 600000L)
  public void syncUserGroups() {
    Map<String, String> emailSessionKeyMap = [:]
    Map<String, Collection<String>> emailCurrentGroupsMap = [:]
    Set<String> sessionKeys = redisTemplate.keys('*session:sessions*')

    sessionKeys.each { String key ->
      def secCtx = redisTemplate.opsForHash().get(key, "sessionAttr:SPRING_SECURITY_CONTEXT") as SecurityContext
      def principal = secCtx?.authentication?.principal
      if (principal && principal instanceof User) {
        emailSessionKeyMap[principal.email] = key
        emailCurrentGroupsMap[principal.email] = principal.roles
      }
    }

    def newGroupsMap = userRolesProvider.multiLoadRoles(emailSessionKeyMap.keySet())
    def sessionKeysToDelete = []
    newGroupsMap.each { String email, Collection<String> newGroups ->
      // cast for equals check to work
      List<String> newList = newGroups as List
      List<String> oldList = emailCurrentGroupsMap[email] as List
      if (oldList != newList) {
        sessionKeysToDelete.add(emailSessionKeyMap[email])
      }
    }
    redisTemplate.delete(sessionKeysToDelete)
    log.info("Invalidated {} user sessions due to changed group memberships.", sessionKeysToDelete.size())
  }
}
