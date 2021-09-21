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

import com.google.gson.Gson;
import com.opsmx.spinnaker.gate.enums.AuditEventType;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.security.AbstractAuthenticationAuditListener;
import org.springframework.security.authentication.event.*;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationAuditListener extends AbstractAuthenticationAuditListener {

  @Autowired private AuditHandler auditHandler;

  private Gson gson = new Gson();

  @Override
  public void onApplicationEvent(AbstractAuthenticationEvent event) {

    try {
      Map<String, Object> authEvent =
          gson.fromJson(gson.toJson(event, AbstractAuthenticationEvent.class), Map.class);
      log.info("Authentication audit events received : {}", authEvent);
      if (event.getAuthentication().isAuthenticated()
          && event instanceof AuthenticationSuccessEvent) {

        log.info("auth success audit : {}", event);
        auditHandler.publishEvent(AuditEventType.AUTHENTICATION_SUCCESSFUL_AUDIT, event);

      } else if (!event.getAuthentication().isAuthenticated()
          && event instanceof AbstractAuthenticationFailureEvent) {
        AuthenticationFailureBadCredentialsEvent authenticationFailureBadCredentialsEvent =
            (AuthenticationFailureBadCredentialsEvent) event;
        Map<String, Object> auditData =
            gson.fromJson(
                gson.toJson(
                    authenticationFailureBadCredentialsEvent,
                    AuthenticationFailureBadCredentialsEvent.class),
                Map.class);
        log.info("auth failure audit : {}", auditData);
        auditHandler.publishEvent(AuditEventType.AUTHENTICATION_FAILURE_AUDIT, auditData);

      } else if (event instanceof LogoutSuccessEvent) {
        LogoutSuccessEvent logoutSuccessEvent = (LogoutSuccessEvent) event;
        Map<String, Object> auditData =
            gson.fromJson(gson.toJson(logoutSuccessEvent, LogoutSuccessEvent.class), Map.class);
        log.info("logout success audit : {}", auditData);
        auditHandler.publishEvent(AuditEventType.SUCCESSFUL_USER_LOGOUT_AUDIT, auditData);
      }

    } catch (Exception e) {
      log.error("Exception occured while capturing audit events : {}", e);
    }
  }
}
