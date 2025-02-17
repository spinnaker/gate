/*
 * Copyright 2025 OpsMx, Inc.
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

package com.netflix.spinnaker.gate.security.oauth2;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class SpinnakerUserInfoTokenServicesTest {

  private SpinnakerUserInfoTokenServices tokenServices;
  private OAuth2SsoConfig.UserInfoRequirements userInfoRequirements;

  @BeforeEach
  public void setUp() {
    tokenServices = new SpinnakerUserInfoTokenServices();
    userInfoRequirements = new OAuth2SsoConfig.UserInfoRequirements();
    tokenServices.userInfoRequirements = userInfoRequirements;
  }

  @Test
  public void shouldEvaluateUserInfoRequirementsAgainstAuthenticationDetails() {
    // No domain restriction, everything should match
    assertTrue(tokenServices.hasAllUserInfoRequirements(Map.of()));
    assertTrue(tokenServices.hasAllUserInfoRequirements(Map.of("hd", "foo.com")));
    assertTrue(tokenServices.hasAllUserInfoRequirements(Map.of("bar", "foo.com")));
    assertTrue(tokenServices.hasAllUserInfoRequirements(Map.of("bar", "bar.com")));

    // Domain restricted but not found
    userInfoRequirements.put("hd", "foo.com");
    assertFalse(tokenServices.hasAllUserInfoRequirements(Map.of()));
    assertTrue(tokenServices.hasAllUserInfoRequirements(Map.of("hd", "foo.com")));
    assertFalse(tokenServices.hasAllUserInfoRequirements(Map.of("bar", "foo.com")));
    assertFalse(tokenServices.hasAllUserInfoRequirements(Map.of("bar", "bar.com")));

    // Domain restricted by regex
    userInfoRequirements.put("hd", "/foo\\.com|bar\\.com/");
    assertFalse(tokenServices.hasAllUserInfoRequirements(Map.of()));
    assertTrue(tokenServices.hasAllUserInfoRequirements(Map.of("hd", "foo.com")));
    assertTrue(tokenServices.hasAllUserInfoRequirements(Map.of("hd", "bar.com")));
    assertFalse(tokenServices.hasAllUserInfoRequirements(Map.of("hd", "baz.com")));
    assertFalse(tokenServices.hasAllUserInfoRequirements(Map.of("bar", "foo.com")));

    // Multiple restriction values
    userInfoRequirements.put("bar", "bar.com");
    assertFalse(tokenServices.hasAllUserInfoRequirements(Map.of("hd", "foo.com")));
    assertFalse(tokenServices.hasAllUserInfoRequirements(Map.of("bar", "bar.com")));
    assertTrue(tokenServices.hasAllUserInfoRequirements(Map.of("hd", "foo.com", "bar", "bar.com")));

    // Evaluating a list
    userInfoRequirements.clear();
    userInfoRequirements.put("roles", "expected-role");
    assertTrue(tokenServices.hasAllUserInfoRequirements(Map.of("roles", "expected-role")));
    assertTrue(
        tokenServices.hasAllUserInfoRequirements(
            Map.of("roles", List.of("expected-role", "unexpected-role"))));
    assertFalse(tokenServices.hasAllUserInfoRequirements(Map.of()));
    assertFalse(tokenServices.hasAllUserInfoRequirements(Map.of("roles", "unexpected-role")));
    assertFalse(
        tokenServices.hasAllUserInfoRequirements(Map.of("roles", List.of("unexpected-role"))));

    // Evaluating a regex in a list
    userInfoRequirements.put("roles", "/^.+_ADMIN$/");
    assertTrue(tokenServices.hasAllUserInfoRequirements(Map.of("roles", "foo_ADMIN")));
    assertTrue(tokenServices.hasAllUserInfoRequirements(Map.of("roles", List.of("foo_ADMIN"))));
    assertFalse(
        tokenServices.hasAllUserInfoRequirements(Map.of("roles", List.of("_ADMIN", "foo_USER"))));
    assertFalse(
        tokenServices.hasAllUserInfoRequirements(
            Map.of("roles", List.of("foo_ADMINISTRATOR", "bar_USER"))));
  }

  @ParameterizedTest
  @MethodSource("provideRoleData")
  public void shouldExtractRolesFromDetails(Object rolesValue, List<String> expectedRoles) {
    SpinnakerUserInfoTokenServices tokenServices = new SpinnakerUserInfoTokenServices();
    tokenServices.userInfoMapping = new OAuth2SsoConfig.UserInfoMapping();
    tokenServices.userInfoMapping.setRoles("roles");
    Map<String, Object> details = new HashMap<>();
    details.put("roles", rolesValue);
    assertEquals(expectedRoles, tokenServices.getRoles(details));
  }

  private static Stream<Arguments> provideRoleData() {
    return Stream.of(
        Arguments.of(null, List.of()),
        Arguments.of("", List.of()),
        Arguments.of(List.of("foo", "bar"), List.of("foo", "bar")),
        Arguments.of("foo,bar", List.of("foo", "bar")),
        Arguments.of("foo bar", List.of("foo", "bar")),
        Arguments.of("foo", List.of("foo")),
        Arguments.of("foo   bar", List.of("foo", "bar")),
        Arguments.of("foo,,,bar", List.of("foo", "bar")),
        Arguments.of("foo, bar", List.of("foo", "bar")),
        Arguments.of(List.of("[]"), List.of()),
        Arguments.of(List.of("[\"foo\"]"), List.of("foo")),
        Arguments.of(List.of("[\"foo\", \"bar\"]"), List.of("foo", "bar")),
        Arguments.of(1, List.of()),
        Arguments.of(Map.of("blergh", "blarg"), List.of()));
  }
}
