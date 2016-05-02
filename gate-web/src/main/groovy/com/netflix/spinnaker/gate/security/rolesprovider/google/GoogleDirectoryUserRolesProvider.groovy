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

package com.netflix.spinnaker.gate.security.rolesprovider.google

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.admin.directory.Directory
import com.google.api.services.admin.directory.DirectoryScopes
import com.google.api.services.admin.directory.model.Group
import com.google.api.services.admin.directory.model.Groups
import com.netflix.spinnaker.gate.security.rolesprovider.SpinnakerUserRolesProvider
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.util.Assert

@Component
@ConditionalOnProperty(value = "auth.groupMembership.service", havingValue = "google")
class GoogleDirectoryUserRolesProvider implements SpinnakerUserRolesProvider, InitializingBean {

  @Autowired
  GoogleDirectoryUserRolesProviderConfig config

  private static final Collection<String> SERVICE_ACCOUNT_SCOPES = [DirectoryScopes.ADMIN_DIRECTORY_GROUP_READONLY]

  @Override
  void afterPropertiesSet() throws Exception {
    Assert.state(config.serviceAccountEmail != null, "Supply a service account email")
    Assert.state(config.domain != null, "Supply a domain")
    Assert.state(config.adminUsername != null, "Supply an admin username")
    Assert.state(config.credentialPath != null, "Supply an service account credentials path")
  }

  @Override
  Collection<String> loadRoles(String userEmail) {
    Directory service = getDirectoryService()
    Groups groups = service.groups().list().setDomain(config.domain).setUserKey(userEmail).execute()
    return groups.getGroups().collect { Group g -> g.getName() }
  }

  Directory getDirectoryService() {
    HttpTransport httpTransport = new NetHttpTransport()
    JacksonFactory jsonFactory = new JacksonFactory()
    GoogleCredential credential = new GoogleCredential.Builder()
      .setServiceAccountUser(config.adminUsername)
      .setTransport(httpTransport)
      .setJsonFactory(jsonFactory)
      .setServiceAccountId(config.serviceAccountEmail)
      .setServiceAccountPrivateKeyFromP12File(new File(config.credentialPath))
      .setServiceAccountScopes(SERVICE_ACCOUNT_SCOPES)
      .build()

    return new Directory.Builder(httpTransport, jsonFactory, credential)
      .setApplicationName("Spinnaker-Gate")
      .build()
  }
}
