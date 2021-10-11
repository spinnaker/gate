/*
 * Copyright 2021 Netflix, Inc.
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
import com.opsmx.spinnaker.gate.util.FileUtil;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.web.context.support.ServletRequestHandledEvent;

@ExtendWith(MockitoExtension.class)
public class UserActivityAuditListenerTest {

  @Mock private AuditHandler auditHandler;

  @InjectMocks private UserActivityAuditListener userActivityAuditListener;

  private ServletRequestHandledEvent servletRequestHandledEvent;
  private String userActivityAuditJsonFile = "json/UserActivityAudit.json";
  private Map<String, Object> auditData;

  @BeforeEach
  public void beforeTest() throws Exception {
    setData();
  }

  private void setData() throws Exception {
    auditData = FileUtil.getFileAsJsonObject(userActivityAuditJsonFile, Map.class);
    servletRequestHandledEvent =
        new ServletRequestHandledEvent(
            new Object(),
            (String) auditData.get("requestUrl"),
            (String) auditData.get("clientAddress"),
            (String) auditData.get("method"),
            (String) auditData.get("servletName"),
            (String) auditData.get("sessionId"),
            (String) auditData.get("userName"),
            (Long) auditData.get("processingTimeMillis"));
  }

  @Test
  @DisplayName(
      "should simply ignore when when event instance type is not ServletRequestHandledEvent")
  public void test1() {
    userActivityAuditListener.onApplicationEvent(
        ArgumentMatchers.any(AbstractAuthenticationEvent.class));
  }

  @Test
  @DisplayName("should handle the ServletRequestHandledEvent and publish the audit")
  public void test2() {
    Mockito.doNothing()
        .when(auditHandler)
        .publishEvent(AuditEventType.USER_ACTIVITY_AUDIT, auditData);
    userActivityAuditListener.onApplicationEvent(servletRequestHandledEvent);
  }
}
