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

import com.netflix.spinnaker.security.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

/**
 * Custom implementation of {@link OidcUser} that integrates with Spinnaker's {@link User} model.
 * This class holds OIDC-related user details such as ID token, user info, and additional
 * attributes.
 *
 * <p>It extends {@link User} from kork to include Spinnaker-specific fields like allowed accounts
 * and roles.
 *
 * <p>Usage: This class is used in OIDC authentication flows where user details are retrieved from
 * an OIDC provider.
 *
 * @author rahul-chekuri
 * @see User
 */
public class SpinnakerOIDCUser extends User implements OidcUser {
  /**
   * Attributes containing user details, retrieved from the OIDC provider. These attributes
   * typically include user profile information such as name, email, and roles.
   */
  private final Map<String, Object> attributes;

  /** Authorities assigned to the user, used for authorization in Spring Security. */
  private final List<GrantedAuthority> authorities;

  private final OidcIdToken idToken;
  private final OidcUserInfo userInfo;

  public SpinnakerOIDCUser(
      String email,
      String firstName,
      String lastName,
      Collection<String> allowedAccounts,
      List<String> roles,
      String username,
      OidcIdToken idToken,
      OidcUserInfo userInfo,
      Map<String, Object> attributes,
      List<GrantedAuthority> authorities) {
    this.idToken = idToken;
    this.userInfo = userInfo;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.allowedAccounts = allowedAccounts;
    this.roles = roles;
    this.username = username;
    this.attributes =
        attributes != null
            ? Collections.unmodifiableMap(new HashMap<>(attributes))
            : Collections.emptyMap();
    this.authorities =
        authorities != null
            ? Collections.unmodifiableList(new ArrayList<>(authorities))
            : Collections.emptyList();
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public List<GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getName() {
    return super.getUsername();
  }

  @Override
  public Map<String, Object> getClaims() {
    return this.attributes;
  }

  @Override
  public OidcUserInfo getUserInfo() {
    return this.userInfo;
  }

  @Override
  public OidcIdToken getIdToken() {
    return this.idToken;
  }
}
