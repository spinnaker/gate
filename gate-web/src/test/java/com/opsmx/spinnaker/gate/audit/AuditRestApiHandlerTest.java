/*
 * Copyright 2021 OpsMx, Inc.
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

package com.opsmx.spinnaker.gate.audit;

import com.opsmx.spinnaker.gate.enums.AuditEventType;
import com.opsmx.spinnaker.gate.feignclient.AuditService;
import com.opsmx.spinnaker.gate.model.OesAuditModel;
import com.opsmx.spinnaker.gate.util.FileUtil;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class AuditRestApiHandlerTest {

  @Mock private AuditService auditServiceClient;

  @InjectMocks private AuditRestApiHandler auditRestApiHandler;

  private String authenticationAuditEventJsonFile = "/json/authSuccess.json";

  private Map<String, Object> authAuditData = new HashMap<>();

  @BeforeEach
  public void beforeTest() throws Exception {
    setAuthAuditData();
  }

  private void setAuthAuditData() throws Exception {
    authAuditData = FileUtil.getFileAsJsonObject(authenticationAuditEventJsonFile, Map.class);
  }

  @Test
  public void shouldPublishAuthAuditData() {
    Object expectedResponse = new Object();
    Mockito.when(
            auditServiceClient.publishAuditData(
                ArgumentMatchers.any(OesAuditModel.class), ArgumentMatchers.anyString()))
        .thenReturn(ResponseEntity.accepted().body(expectedResponse));
    auditRestApiHandler.publishEvent(AuditEventType.AUTHENTICATION_SUCCESSFUL_AUDIT, authAuditData);
  }
}
