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

package com.netflix.spinnaker.gate.security.userdetailsservice.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.admin.directory.model.Group;
import com.google.api.services.admin.directory.model.Groups;
import com.netflix.spinnaker.gate.security.AnonymousAccountsService;
import com.netflix.spinnaker.gate.security.oauth2.client.OAuth2ClientConfig;
import com.netflix.spinnaker.gate.security.userdetailsservice.SpinnakerUserProvider;
import com.netflix.spinnaker.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(value = "auth.groupMembership.service", havingValue = "google")
public class GoogleDirectoryUserProvider implements SpinnakerUserProvider {

  @Autowired
  private GoogleDirectoryUserProviderConfig config;

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private AnonymousAccountsService anonymousAccountsService;

  @Autowired
  private OAuth2ClientConfig oAuth2Configuration;

  private static final String[] SERVICE_ACCOUNT_SCOPES = {DirectoryScopes.ADMIN_DIRECTORY_GROUP_READONLY};

  private static final String[] USER_ROLES = {"user"};

  private Directory getDirectoryService() throws GeneralSecurityException,
    IOException, URISyntaxException {
    HttpTransport httpTransport = new NetHttpTransport();
    JacksonFactory jsonFactory = new JacksonFactory();
    GoogleCredential credential = new GoogleCredential.Builder()
      .setServiceAccountUser(config.getAdminUsername())
      .setTransport(httpTransport)
      .setJsonFactory(jsonFactory)
      .setServiceAccountId(config.getServiceAccountEmail())
      .setServiceAccountPrivateKeyFromP12File(new java.io.File(config.getCredentialPath()))
      .setServiceAccountScopes(Arrays.asList(SERVICE_ACCOUNT_SCOPES))
      .build();

    return new Directory.Builder(httpTransport, jsonFactory, credential)
      .setApplicationName("Spinnaker")
      .build();
  }

  @Override
  public User loadUser(String tokenValue) {
    String userEmail = loadEmail(tokenValue);
    Groups groups = null;
    // TODO(jacobkiefer): refactor and deal with exceptions properly
    try {
      groups = loadGroups(userEmail);
    } catch (Exception e) {
      e.printStackTrace();
    }

    List<String> allowedAccounts = new ArrayList<String>();
    for (Group group : groups.getGroups()) {
      allowedAccounts.add(group.getName());
    }

    // allow this user to user the anonymous accounts also
    for (String s : anonymousAccountsService.getAllowedAccounts()) {
      allowedAccounts.add(s);
    }
    return new User(userEmail, null, null, Arrays.asList(USER_ROLES), allowedAccounts);
  }

  private String loadEmail(String tokenValue) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Bearer " + tokenValue);
    HttpEntity infoReq = new HttpEntity(headers);
    ResponseEntity<Map> userInfo = restTemplate.exchange(oAuth2Configuration.getUserInfoUri(), HttpMethod.GET, infoReq, Map.class);
    Map<String, ?> userData = userInfo.getBody();
    return (String) userData.get("email");
  }

  private Groups loadGroups(String userEmail) throws GeneralSecurityException, IOException, URISyntaxException {
    Directory service = getDirectoryService();

    return service.groups().list().setDomain(config.getDomain()).setUserKey(userEmail).execute();
  }

}
