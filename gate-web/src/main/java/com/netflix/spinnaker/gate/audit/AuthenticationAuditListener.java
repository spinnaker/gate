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

package com.netflix.spinnaker.gate.audit;

import com.google.gson.Gson;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.security.AbstractAuthenticationAuditListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthenticationAuditListener extends AbstractAuthenticationAuditListener {

  private Gson gson = new Gson();

  @Override
  public void onApplicationEvent(AbstractAuthenticationEvent event) {
    log.info("Authentication audit events received : {}", event.getAuthentication());
    try {
      //      User user = (User) event.getAuthentication().getPrincipal();
      Map<String, Object> principal =
          gson.fromJson(gson.toJson(event.getAuthentication().getPrincipal()), Map.class);
      Map<String, Object> details =
          gson.fromJson(gson.toJson(event.getAuthentication().getDetails()), Map.class);

      log.info("principal : {}", principal);
      log.info("details : {}", details);
    } catch (Exception e) {
      log.error("Exception occured while capturing audit events : {}", e);
    }
  }
}
