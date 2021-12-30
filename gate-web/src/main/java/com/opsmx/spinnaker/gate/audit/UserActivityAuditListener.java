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
import com.opsmx.spinnaker.gate.enums.OesServices;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.ServletRequestHandledEvent;

@Slf4j
@Component
@EnableAsync
public class UserActivityAuditListener implements ApplicationListener {

  @Autowired private AuditHandler auditHandler;

  @Async
  @Override
  public void onApplicationEvent(ApplicationEvent event) {

    try {
      log.debug("event received ");
      if (event instanceof ServletRequestHandledEvent) {
        ServletRequestHandledEvent servletRequestHandledEvent = (ServletRequestHandledEvent) event;
        if (isAuthenticatedRequest(servletRequestHandledEvent)) {
          log.debug("request is authenticated");
          String baseUrl = getBaseUrl(servletRequestHandledEvent.getRequestUrl());
          log.debug("base url : {}", baseUrl);
          if (isOesActivity(baseUrl)) {
            Map<String, Object> auditData = populateAuditData(servletRequestHandledEvent);
            log.debug("publishing the event to audit service : {}", auditData);
            auditHandler.publishEvent(AuditEventType.USER_ACTIVITY_AUDIT, auditData);
          }
        }
      }
    } catch (Exception e) {
      log.error("Excepion occured : {}", e);
    }
  }

  private boolean isOesActivity(String baseUrl) {

    boolean flag = Boolean.FALSE;
    try {
      switch (OesServices.valueOf(baseUrl)) {
        case oes:
        case autopilot:
        case platformservice:
        case dashboardservice:
        case visibilityservice:
        case auditclientservice:
          flag = Boolean.TRUE;
          break;
      }
    } catch (Exception e) {
      log.debug("Not oes event : {}", e.getMessage());
    }
    return flag;
  }

  private String getBaseUrl(String url) {

    String baseUrl = "";
    if (url != null) {
      String[] urlComponents = url.split("/");
      if (urlComponents != null && urlComponents.length > 1) {
        baseUrl = urlComponents[1];
      }
    }
    return baseUrl;
  }

  private boolean isAuthenticatedRequest(ServletRequestHandledEvent servletRequestHandledEvent) {

    boolean flag = Boolean.FALSE;
    if (servletRequestHandledEvent.getUserName() != null
        && !servletRequestHandledEvent.getUserName().trim().isEmpty()
        && servletRequestHandledEvent.getSessionId() != null
        && !servletRequestHandledEvent.getSessionId().trim().isEmpty()) {
      flag = Boolean.TRUE;
    }
    return flag;
  }

  private Map<String, Object> populateAuditData(
      ServletRequestHandledEvent servletRequestHandledEvent) {
    Map<String, Object> auditData = new HashMap<>();
    auditData.put("requestUrl", servletRequestHandledEvent.getRequestUrl());
    auditData.put("description", servletRequestHandledEvent.getDescription());
    auditData.put("method", servletRequestHandledEvent.getMethod());
    auditData.put("servletName", servletRequestHandledEvent.getServletName());
    auditData.put("userName", servletRequestHandledEvent.getUserName());
    auditData.put("clientAddress", servletRequestHandledEvent.getClientAddress());
    auditData.put("shortDescription", servletRequestHandledEvent.getShortDescription());
    auditData.put("statusCode", servletRequestHandledEvent.getStatusCode());
    auditData.put(
        "failureCase",
        servletRequestHandledEvent.getFailureCause() != null
            ? servletRequestHandledEvent.getFailureCause().getMessage()
            : "");
    auditData.put("processingTimeMillis", servletRequestHandledEvent.getProcessingTimeMillis());
    auditData.put("sessionId", servletRequestHandledEvent.getSessionId());
    auditData.put("timestamp", servletRequestHandledEvent.getTimestamp());
    return auditData;
  }
}
