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

package com.netflix.spinnaker.gate.services

import com.netflix.spinnaker.gate.services.commands.HystrixFactory
import com.netflix.spinnaker.gate.services.internal.ClouddriverService
import com.netflix.spinnaker.gate.services.internal.OrcaService
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@CompileStatic
@Component
class SecurityGroupService {
  private static final String GROUP = "security"

  @Autowired
  ClouddriverService clouddriverService

  @Autowired
  OrcaService orcaService

  /**
   * Keyed by account
   */
  Map getAll() {
    HystrixFactory.newMapCommand(GROUP, "getAllSecurityGroups") {
      clouddriverService.securityGroups
    } execute()
  }

  /**
   * Looks for a security group by its id (uses clouddriver's search api)
   *
   * @param id
   * @return
   */
  Map getById(String id) {
    HystrixFactory.newMapCommand(GROUP, "getSecurityGroupById".toString()) {
      def result = clouddriverService.search(id, "securityGroups", null, 10000, 1)[0]
      if (result.results) {
        Map firstResult = ((List<Map>)result.results)[0]
        String uriString = firstResult.url
        String vpcId = firstResult.vpcId
        def uri = new URI(uriString)
        def path = uri.path
        def query = uri.query
        def region = query.split('=')[1]
        def pathParts = path.split('/')
        def account = pathParts[2]
        def provider = pathParts[3]
        def sgName = pathParts[-1]
        return getSecurityGroup(account, provider, sgName, region, vpcId)
      } else {
        [:]
      }
    } execute()
  }

  /**
   * @param account account name
   * @param provider provider name (aws, gce, docker)
   * @param region optional. nullable
   */
  Map getForAccountAndProviderAndRegion(String account, String provider, String region) {
    HystrixFactory.newMapCommand(GROUP, "getSecurityGroupsForAccountAndProvider-$provider") {
      clouddriverService.getSecurityGroups(account, provider, region)
    } execute()
  }

  /**
   * @param account account name
   * @param provider provider name (aws, gce, docker)
   * @param name security group name
   * @param region optional. nullable
   */
  Map getSecurityGroup(String account, String provider, String name, String region, String vpcId = null) {
    HystrixFactory.newMapCommand(GROUP, "getSecurityGroupByIdentifiers-$provider") {
      clouddriverService.getSecurityGroup(account, provider, name, region, vpcId)
    } execute()
  }
}
