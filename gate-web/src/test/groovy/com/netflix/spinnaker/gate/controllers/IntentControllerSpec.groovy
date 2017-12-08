/*
 * Copyright 2017 Netflix, Inc.
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

package com.netflix.spinnaker.gate.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spinnaker.gate.services.IntentService
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import spock.lang.Specification

import javax.ws.rs.core.MediaType

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

class IntentControllerSpec extends Specification {
  def "should update an intent"() {
    given:
    def intentService = Mock(IntentService)
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new IntentController(intentService: intentService)).build()

    and:
    def intent = [
      kind: "Application",
      schema: "1",
      spec: [
        kind: "Application",
        name: "emilyemilyemily",
        description: "EMILY",
        email: "emburns@netflix.com",
        owner: "Emily Burns",
        chaosMonkey: [
          enabled: false,
          meanTimeBetweenKillsInWorkDays: 2,
          minTimeBetweenKillsInWorkDays: 2,
          grouping: "cluster",
          regionsAreIndependent: true,
          exceptions: []
        ],
        enableRestartRunningExecutions: false,
        instanceLinks: [],
        instancePort: 7111,
        appGroup: "spinnaker",
        dataSources: [
          enabled: [],
          disabled: []
        ],
        requiredGroupMembership: [],
        group: "Spinnaker",
        providerSettings: [:],
        trafficGuards: [],
        platformHealthOnlyShowOverride: false,
        platformHealthOnly: false
      ]
    ]

    def intentRequest = [
        intents: [
          intent
        ],
        dryRun: false
    ]


    when:
    def response = mockMvc.perform(
      post("/intents").contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(intentRequest))
    ).andReturn().response

    then:
    1 * intentService.upsertIntent(intentRequest) >> [ [ intentId: "myintentId", intentStatus: "ACTIVE" ] ]
    response.status == 200

  }

  def "should get an intent"() {
    given:
    def intentService = Mock(IntentService)
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new IntentController(intentService: intentService)).build()

    def intent = [
      kind: "Application",
      schema: "1",
      spec: [
        kind: "Application",
        name: "emilyemilyemily",
        description: "EMILY",
        email: "emburns@netflix.com",
        owner: "Emily Burns",
        chaosMonkey: [
          enabled: false,
          meanTimeBetweenKillsInWorkDays: 2,
          minTimeBetweenKillsInWorkDays: 2,
          grouping: "cluster",
          regionsAreIndependent: true,
          exceptions: []
        ],
        enableRestartRunningExecutions: false,
        instanceLinks: [],
        instancePort: 7111,
        appGroup: "spinnaker",
        dataSources: [
          enabled: [],
          disabled: []
        ],
        requiredGroupMembership: [],
        group: "Spinnaker",
        providerSettings: [:],
        trafficGuards: [],
        platformHealthOnlyShowOverride: false,
        platformHealthOnly: false
      ]
    ]

    when:
    def response = mockMvc.perform(get("/intents/myintent")).andReturn().response

    then:
    1 * intentService.getIntent("myintent") >> intent
  }

  def "should delete an intent"() {
    given:
    def intentService = Mock(IntentService)
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new IntentController(intentService: intentService)).build()

    when:
    def response = mockMvc.perform(delete("/intents/myintent")).andReturn().response

    then:
    1 * intentService.deleteIntent("myintent")
  }

}
