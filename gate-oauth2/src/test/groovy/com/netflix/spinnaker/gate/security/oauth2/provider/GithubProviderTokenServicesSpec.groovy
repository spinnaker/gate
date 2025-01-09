/*
 * Copyright 2022 Armory, Inc.
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

package com.netflix.spinnaker.gate.security.oauth2.provider

import spock.lang.Specification
import spock.lang.Subject
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.OAuth2ClientContext
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties
import com.netflix.spinnaker.gate.security.oauth2.provider.GithubProviderTokenServices.GithubRequirements
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

class GithubProviderTokenServicesSpec extends Specification {

  def 'should find org membership for single-page API response'() {
    setup:
    def sso = Mock(ResourceServerProperties)
    sso.getClientId() >> 'testClientId'
    def requirements = new GithubRequirements()
    requirements.organization = 'testOrg'
    def restTemplate = Mock(OAuth2RestTemplate)
    def clientContext = Mock(OAuth2ClientContext)
    restTemplate.getOAuth2ClientContext() >> clientContext
    clientContext.getAccessToken() >> Mock(OAuth2AccessToken)
    @Subject tokenServices = new GithubProviderTokenServices(sso: sso, requirements: requirements)
    tokenServices.restTemplate = restTemplate

    when: 'the user orgs can be contained in a single-page API response'
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    def responseEntity = new ResponseEntity<List<Map<String, String>>>([['login': 'testOrg']], headers, HttpStatus.OK);
    restTemplate.getForEntity('https://github.com/api/v3/users/1234/orgs', List.class) >> responseEntity

    and: 'an API request is made'
    boolean isMember = tokenServices.checkOrganization('testToken', 'https://github.com/api/v3/users/1234/orgs', 'testOrg')

    then: 'the organization membership is found'
    assert isMember
  }

  def 'should not find org membership for single-page API response'() {
    setup:
    def sso = Mock(ResourceServerProperties)
    sso.getClientId() >> 'testClientId'
    def requirements = new GithubRequirements()
    requirements.organization = 'testOrg'
    def restTemplate = Mock(OAuth2RestTemplate)
    def clientContext = Mock(OAuth2ClientContext)
    restTemplate.getOAuth2ClientContext() >> clientContext
    clientContext.getAccessToken() >> Mock(OAuth2AccessToken)
    @Subject tokenServices = new GithubProviderTokenServices(sso: sso, requirements: requirements)
    tokenServices.restTemplate = restTemplate

    when: 'the user orgs can be contained in a single-page API response'
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    def response = new ResponseEntity<List<Map<String, String>>>([['login': 'otherOrg']], headers, HttpStatus.OK);
    restTemplate.getForEntity('https://github.com/api/v3/users/1234/orgs', List.class) >> response

    and: 'an API request is made'
    boolean isMember = tokenServices.checkOrganization('testToken', 'https://github.com/api/v3/users/1234/orgs', 'testOrg')

    then: 'the organization membership is not found'
    assert !isMember
  }

  def 'should find org membership for multi-page API response'() {
    setup:
    def sso = Mock(ResourceServerProperties)
    sso.getClientId() >> 'testClientId'
    def requirements = new GithubRequirements()
    requirements.organization = 'testOrg'
    def restTemplate = Mock(OAuth2RestTemplate)
    def clientContext = Mock(OAuth2ClientContext)
    restTemplate.getOAuth2ClientContext() >> clientContext
    clientContext.getAccessToken() >> Mock(OAuth2AccessToken)
    @Subject tokenServices = new GithubProviderTokenServices(sso: sso, requirements: requirements)
    tokenServices.restTemplate = restTemplate

    when: 'the user orgs are contained in a multi-page API response'
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add('Link', '<https://github.com/api/v3/users/1234/orgs?page=2>; rel="next", <https://github.com/api/v3/user/1234/orgs?page=3>; rel="last"')
    def firstResponse = new ResponseEntity<List<Map<String, String>>>([['login': 'otherOrg']], headers, HttpStatus.OK);
    restTemplate.getForEntity('https://github.com/api/v3/users/1234/orgs', List.class) >> firstResponse
    def secondResponse = new ResponseEntity<List<Map<String, String>>>([['login': 'testOrg']], headers, HttpStatus.OK);
    restTemplate.getForEntity('https://github.com/api/v3/users/1234/orgs?page=2', List.class) >> secondResponse

    and: 'API requests are made'
    boolean isMember = tokenServices.checkOrganization('testToken', 'https://github.com/api/v3/users/1234/orgs', 'testOrg')

    then: 'the organization membership is found'
    assert isMember
  }

  def 'should not find org membership for multi-page API response'() {
    setup:
    def sso = Mock(ResourceServerProperties)
    sso.getClientId() >> 'testClientId'
    def requirements = new GithubRequirements()
    requirements.organization = 'testOrg'
    def restTemplate = Mock(OAuth2RestTemplate)
    def clientContext = Mock(OAuth2ClientContext)
    restTemplate.getOAuth2ClientContext() >> clientContext
    clientContext.getAccessToken() >> Mock(OAuth2AccessToken)
    @Subject tokenServices = new GithubProviderTokenServices(sso: sso, requirements: requirements)
    tokenServices.restTemplate = restTemplate

    when: 'the user orgs are contained in a multi-page API response'
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add('Link', '<https://github.com/api/v3/users/1234/orgs?page=2>; rel="next", <https://github.com/api/v3/users/1234/orgs?page=3>; rel="last"')
    def firstResponse = new ResponseEntity<List<Map<String, String>>>([['login': 'otherOrg']], headers, HttpStatus.OK);
    restTemplate.getForEntity('https://github.com/api/v3/users/1234/orgs', List.class) >> firstResponse
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add('Link', '<https://github.com/api/v3/users/1234/orgs?page=3>; rel="last", <https://github.com/api/v3/users/1234/orgs?page=1>; rel="first"')
    def secondResponse = new ResponseEntity<List<Map<String, String>>>([['login': 'anotherOrg']], headers, HttpStatus.OK);
    restTemplate.getForEntity('https://github.com/api/v3/users/1234/orgs?page=2', List.class) >> secondResponse

    and: 'API requests are made'
    boolean isMember = tokenServices.checkOrganization('testToken', 'https://github.com/api/v3/users/1234/orgs', 'testOrg')

    then: 'the organization membership is found'
    assert !isMember
  }


}
