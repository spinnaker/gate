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
import com.opsmx.spinnaker.gate.constant.CamelEndpointConstant;
import com.opsmx.spinnaker.gate.enums.AuditEventType;
import com.opsmx.spinnaker.gate.model.AuditData;
import com.opsmx.spinnaker.gate.model.OesAuditModel;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.security.AbstractAuthenticationAuditListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.event.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableAsync
public class AuthenticationAuditListener extends AbstractAuthenticationAuditListener {

  @Autowired private AuditHandler auditHandler;
  @Autowired @Lazy private ProducerTemplate template;

  Gson gson = new Gson();

  @Async
  @Override
  public void onApplicationEvent(AbstractAuthenticationEvent event) {

    try {
      log.debug("Authentication audit events received : {}", event);
      // OP-17106: looks like a saml event fetch name and roles to publish
      if (event.getAuthentication().isAuthenticated()
          && event instanceof InteractiveAuthenticationSuccessEvent) {
        log.debug("publishEvent InteractiveAuthenticationSuccessEvent");
        handleAuthenticationEvent(event, AuditEventType.AUTHENTICATION_SUCCESSFUL_AUDIT);
        return;
      }

      if (event.getAuthentication().isAuthenticated()
          && event instanceof AuthenticationSuccessEvent) {
        log.debug("publishEvent AuthenticationSuccessEvent");
        template.asyncSendBody(
            CamelEndpointConstant.directUserActivity,
            auditHandler.publishEvent(AuditEventType.AUTHENTICATION_SUCCESSFUL_AUDIT, event));
      } else if (!event.getAuthentication().isAuthenticated()
          && event instanceof AbstractAuthenticationFailureEvent) {
        log.debug("publishEvent AbstractAuthenticationFailureEvent");
        template.asyncSendBody(
            CamelEndpointConstant.directUserActivity,
            auditHandler.publishEvent(AuditEventType.AUTHENTICATION_FAILURE_AUDIT, event));
      } else if (event instanceof LogoutSuccessEvent) {
        if (event
            .getAuthentication()
            .getClass()
            .getName()
            .equals("org.springframework.security.providers.ExpiringUsernameAuthenticationToken")) {
          log.debug("publishEvent LogoutSuccessEvent with ExpiringUsernameAuthenticationToken");
          handleAuthenticationEvent(event, AuditEventType.SUCCESSFUL_USER_LOGOUT_AUDIT);
          return;
        }
        log.debug("publishEvent LogoutSuccessEvent");
        auditHandler.publishEvent(AuditEventType.SUCCESSFUL_USER_LOGOUT_AUDIT, event);
        AbstractAuthenticationToken auth = (AbstractAuthenticationToken) event.getAuthentication();
        String name = auth.getName();
        template.asyncSendBody(
            CamelEndpointConstant.directUserActivity,
            getOesAuditModel(
                name, event.getTimestamp(), AuditEventType.SUCCESSFUL_USER_LOGOUT_AUDIT));
      }

    } catch (Exception e) {
      log.error("Exception occurred while capturing audit events : {}", e);
    }
  }

  private void handleAuthenticationEvent(
      AbstractAuthenticationEvent event, AuditEventType eventType) {
    AbstractAuthenticationToken auth = (AbstractAuthenticationToken) event.getAuthentication();
    String name = auth.getName();
    List<String> roles =
        Optional.ofNullable(auth.getAuthorities()).orElse(new ArrayList<>()).stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
    AuditData data = new AuditData(name, roles, event.getTimestamp());
    auditHandler.publishEvent(eventType, data);
    template.asyncSendBody(
        CamelEndpointConstant.directUserActivity,
        getOesAuditModel(name, event.getTimestamp(), eventType));
  }

  private String getOesAuditModel(String name, Long timestamp, AuditEventType eventType) {
    OesAuditModel oesAuditModel = new OesAuditModel();
    Map<String, Object> date = new HashMap<>();
    date.put("userName", name);
    date.put("timestamp", timestamp);
    oesAuditModel.setEventId(UUID.randomUUID().toString());
    oesAuditModel.setAuditData(date);
    oesAuditModel.setEventType(eventType);
    String model = gson.toJson(oesAuditModel, OesAuditModel.class);
    log.debug("model: {}", model);
    return model;
  }
}
