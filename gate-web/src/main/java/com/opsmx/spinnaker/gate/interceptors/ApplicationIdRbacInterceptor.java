/*
 * Copyright 2022 OpsMx, Inc.
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

package com.opsmx.spinnaker.gate.interceptors;

import com.opsmx.spinnaker.gate.rbac.ApplicationFeatureRbac;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@ConditionalOnExpression("${rbac.enabled:false}")
public class ApplicationIdRbacInterceptor implements HandlerInterceptor {

  @Autowired private ApplicationFeatureRbac applicationFeatureRbac;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {
    log.info(
        "Request intercepted for authorizing if the user is having enough access to perform the action");
    applicationFeatureRbac.authorizeUserForApplicationId(
        request.getUserPrincipal().getName(), request.getRequestURI(), request.getMethod());

    return true;
  }
}
