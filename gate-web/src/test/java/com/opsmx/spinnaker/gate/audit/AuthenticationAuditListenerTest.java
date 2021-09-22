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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;

@ExtendWith(MockitoExtension.class)
public class AuthenticationAuditListenerTest {

  @Mock private AuditHandler auditHandler;

  @InjectMocks private AuthenticationAuditListener authenticationAuditListener;

  @Test
  public void shouldPublishAuthenticationSuccessEvent() {
    Mockito.doNothing()
        .when(auditHandler)
        .publishEvent(
            ArgumentMatchers.any(AuditEventType.class),
            ArgumentMatchers.any(AuthenticationSuccessEvent.class));
    authenticationAuditListener.onApplicationEvent(
        ArgumentMatchers.any(AuthenticationSuccessEvent.class));
  }

  @Test
  public void shouldPublishAuthenticationFailureEvent() {
    Mockito.doNothing()
        .when(auditHandler)
        .publishEvent(
            ArgumentMatchers.any(AuditEventType.class),
            ArgumentMatchers.any(AbstractAuthenticationFailureEvent.class));
    authenticationAuditListener.onApplicationEvent(
        ArgumentMatchers.any(AbstractAuthenticationFailureEvent.class));
  }

  @Test
  public void shouldPublishLogoutSuccessEvent() {
    Mockito.doNothing()
        .when(auditHandler)
        .publishEvent(
            ArgumentMatchers.any(AuditEventType.class),
            ArgumentMatchers.any(LogoutSuccessEvent.class));
    authenticationAuditListener.onApplicationEvent(ArgumentMatchers.any(LogoutSuccessEvent.class));
  }
}
