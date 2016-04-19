/*
 * Copyright 2015 Netflix, Inc.
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

package com.netflix.spinnaker.gate.security.saml

import com.netflix.spinnaker.gate.services.internal.ClouddriverService
import com.netflix.spinnaker.gate.security.anonymous.AnonymousConfig

import com.netflix.spinnaker.security.User
import org.opensaml.DefaultBootstrap
import spock.lang.Specification
import spock.lang.Unroll

class SAMLSecurityControllerSpec extends Specification {
  def samlResponse = """
<?xml version="1.0" encoding="UTF-8"?>
<samlp:Response xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol">
   <saml:Assertion xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion">
      <saml:Subject>
         <saml:NameID Format="urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress">test@email.com</saml:NameID>
      </saml:Subject>
      <saml:AttributeStatement>
         <saml:Attribute Name="email" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:basic">
            <saml:AttributeValue xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="xs:string">test@email.com</saml:AttributeValue>
         </saml:Attribute>
         <saml:Attribute Name="familyName" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:basic">
            <saml:AttributeValue xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="xs:string">FamilyName</saml:AttributeValue>
         </saml:Attribute>
         <saml:Attribute Name="userName" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:basic">
            <saml:AttributeValue xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="xs:string">test</saml:AttributeValue>
         </saml:Attribute>
         <saml:Attribute Name="givenName" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:basic">
            <saml:AttributeValue xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="xs:string">GivenName</saml:AttributeValue>
         </saml:Attribute>
         <saml:Attribute Name="fullName" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:basic">
            <saml:AttributeValue xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="xs:string">Test User</saml:AttributeValue>
         </saml:Attribute>
         <saml:Attribute Name="memberOf" NameFormat="urn:oasis:names:tc:SAML:2.0:attrname-format:basic">
            <saml:AttributeValue xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="xs:string">CN=groupA,OU=Groups,DC=netflix,DC=com;CN=groupB,DC=netflix,DC=com;</saml:AttributeValue>
            <saml:AttributeValue xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="xs:string">groupC</saml:AttributeValue>
         </saml:Attribute>
      </saml:AttributeStatement>
   </saml:Assertion>
</samlp:Response>
""".trim()

  void setupSpec() {
    DefaultBootstrap.bootstrap()
  }

  @Unroll
  void "should parse LDAP CN's into roles"() {
    given:
    def allAccounts = [
      new ClouddriverService.Account(name: "test", requiredGroupMembership: ["groupA"]),
      allowedAnonymousAccounts.collect { new ClouddriverService.Account(name: it) }
    ].flatten() as List<ClouddriverService.Account>

    def assertion = SAMLUtils.buildAssertion(new String(new org.apache.commons.codec.binary.Base64().encode(samlResponse.bytes)))
    def userAttributeMapping = new SAMLConfig.UserAttributeMapping(
      firstName: "givenName",
      lastName: "familyName",
      roles: "memberOf"
    )

    when:
    def user = SAMLLoginAuthenticator.buildUser(
      assertion, userAttributeMapping, allowedAnonymousAccounts, allAccounts
    )

    then:
    user.email == "test@email.com"
    user.firstName == "GivenName"
    user.lastName == "FamilyName"
    user.roles == ["groupa", "groupb", "groupc"]
    user.allowedAccounts.sort() == expectedAllowedAccounts

    where:
    allowedAnonymousAccounts || expectedAllowedAccounts
    ["anonymous"]            || ["anonymous", "test"]
    []                       || ["test"]
    null                     || ["test"]
  }

  @Unroll
  void "should check whether a user has a required role"() {
    given:
    def anonymousSecurityConfig = new AnonymousConfig()
    def oneLoginSecurityConfig = new SAMLConfig.SAMLSecurityConfigProperties(
      requiredRoles: requiredRoles
    )
    def user = new User(email: email, roles: roles, allowedAccounts: allowedAccounts)

    expect:
    SAMLLoginAuthenticator.hasRequiredRole(anonymousSecurityConfig, oneLoginSecurityConfig, user) == hasRequiredRole

    where:
    email        | requiredRoles | roles    | allowedAccounts || hasRequiredRole
    "authorized" | ["test"]      | ["prod"] | ["account"]     || false // does not have a required role
    "authorized" | ["test"]      | ["prod"] | ["account"]     || false // does not have a required role
    "authorized" | ["test"]      | []       | ["account"]     || false // does not have a required role
    "authorized" | ["test"]      | null     | ["account"]     || false // does not have a required role
    "authorized" | ["test"]      | ["test"] | []              || false // has required role but no allowed accounts
    "authorized" | ["test"]      | ["test"] | null            || false // has required role but no allowed accounts
    "authorized" | ["test"]      | ["test"] | ["account"]     || true  // has required role
    "authorized" | []            | ["test"] | ["account"]     || true  // has an allowed account (no required roles necessary)
    "authorized" | null          | ["test"] | ["account"]     || true  // has an allowed account (no required roles necessary)
    "authorized" | null          | null     | ["account"]     || true  // has an allowed account (no required roles necessary)
    "anonymous"  | null          | ["test"] | ["account"]     || false // anonymous users are forced to login

  }
}
