/*
 * Copyright 2016 Netflix, Inc.
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

import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.spinnaker.gate.services.TaskService
import com.netflix.spinnaker.gate.services.internal.Front50Service
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import retrofit.MockTypedInput
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.converter.JacksonConverter
import spock.lang.Specification
import spock.lang.Unroll

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put

class PipelineControllerSpec extends Specification {

  private Map pipeline = [
    id: "id",
    name: "test pipeline",
    stages: [],
    triggers: [],
    limitConcurrent: true,
    parallel: true,
    index: 4,
    application: "application"
  ]

  @Unroll
  def "should create a pipeline and return status #result for task status #taskStatus"() {
    given:
    def taskSerivce = Mock(TaskService)
    def front50Service = Mock(Front50Service)
    Map inputMap = [
      description: "Save pipeline 'test pipeline'" as String,
      application: 'application',
      job        : [
        [
          type        : 'savePipeline',
          pipeline    : Base64.encoder.encodeToString(new ObjectMapper().writeValueAsString(pipeline).bytes),
          user        : 'anonymous',
          'staleCheck': false
        ]
      ]
    ]
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new PipelineController(objectMapper: new ObjectMapper(), taskService: taskSerivce, front50Service: front50Service)).build()

    when:
    def response = mockMvc.perform(
      post("/pipelines/").contentType(MediaType.APPLICATION_JSON).param('waitForCompletion', waitForCompletion.toString())
        .content(new ObjectMapper().writeValueAsString(pipeline))
    ).andReturn().response

    then:
    response.status == result

    if (waitForCompletion) {
      1 * taskSerivce.createAndWaitForCompletion(inputMap) >> { [id: 'task-id', application: 'application', status: taskStatus] }
    }

    if (!waitForCompletion) {
      1 * taskSerivce.create(inputMap) >> { ['ref': "/pipelines/task-id"] }
    }

    if (result == 200) { // check for empty response body.
      assert response.getContentAsString().length() == 0
    }
    if (result == 202) { // check location header exists.
      assert response.getHeader('Location').equalsIgnoreCase('http://localhost/tasks/task-id')
    }
    0 * _

    where:
    taskStatus      | waitForCompletion ||   result
    'SUCCEEDED'     | true              ||   200
    'BUFFERED'      | true              ||   400
    'TERMINAL'      | true              ||   400
    'SKIPPED'       | true              ||   400
    'STOPPED'       | true              ||   400
    'CANCELED'      | true              ||   400
    'ASYNC'         | false             ||   202
  }

  @Unroll
  def "should update a pipeline and return status #result for task status #taskStatus"() {
    given:
    def taskSerivce = Mock(TaskService)
    def front50Service = Mock(Front50Service)
    def inputMap = [
      description: "Update pipeline 'test pipeline'" as String,
      application: 'application',
      job: [
        [
          type: 'updatePipeline',
          pipeline: Base64.encoder.encodeToString(new ObjectMapper().writeValueAsString(pipeline).bytes),
          user: 'anonymous'
        ]
      ]
    ]
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new PipelineController(objectMapper: new ObjectMapper(), taskService: taskSerivce, front50Service: front50Service)).build()

    when:
    def response = mockMvc.perform(
      put("/pipelines/${pipeline.id}").contentType(MediaType.APPLICATION_JSON).param('waitForCompletion', waitForCompletion.toString())
        .content(new ObjectMapper().writeValueAsString(pipeline))
    ).andReturn().response

    then:
    response.status == result

    if (waitForCompletion) {
      1 * taskSerivce.createAndWaitForCompletion(inputMap) >> { [id: 'task-id', application: 'application', status: taskStatus] }
    }

    if (!waitForCompletion) {
      1 * taskSerivce.create(inputMap) >> { ['ref': "/pipelines/task-id"] }
    }
    if (result == 200) {
      1 * front50Service.getPipelineConfigsForApplication('application', true) >> [['id': 'id']]
    }
    if (result == 200) { // check body exists.
      assert response.getContentAsString().equalsIgnoreCase('{"id":"id"}')
    }
    if (result == 202) { // check location header exists.
      assert response.getHeader('Location').equalsIgnoreCase('http://localhost/tasks/task-id')
    }
    0 * _

    where:
    taskStatus      | waitForCompletion ||   result
    'SUCCEEDED'     | true              ||   200
    'BUFFERED'      | true              ||   400
    'TERMINAL'      | true              ||   400
    'SKIPPED'       | true              ||   400
    'STOPPED'       | true              ||   400
    'CANCELED'      | true              ||   400
    'ASYNC'         | false             ||   202

  }

  def "should propagate pipeline template errors"() {
    given:
    MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new PipelineController(objectMapper: new ObjectMapper())).build()

    and:
    def pipeline = [
      type: 'templatedPipeline',
      config: [:]
    ]
    def mockedHttpException = [
      errors: [
        [
          location: "configuration:stages.meh",
          message: "Stage configuration is unset"
        ]
      ]
    ]

    when:
    mockMvc.perform(
      post("/start").contentType(MediaType.APPLICATION_JSON)
        .content(new ObjectMapper().writeValueAsString(pipeline))
    ).andDo({
      // thanks groovy
      throw RetrofitError.httpError(
        "http://orca",
        new Response("http://orca", 400, "template invalid", [], new MockTypedInput(new JacksonConverter(), mockedHttpException)),
        new JacksonConverter(),
        Object.class
      )
    })

    then:
    def e = thrown(RetrofitError)
    e.body == mockedHttpException
  }
}
