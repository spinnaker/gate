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

package com.opsmx.spinnaker.gate.cache.platform;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorizationCachingImpl implements AuthorizationCaching {

  @Override
  public Map<String, Object> populateAdminAuthCache(String userName, Map<String, Object> response) {
    log.debug("populating admin auth cache");
    return response;
  }

  @Override
  public Map<String, Object> getRecordFromAdminAuthCache(String userName) {
    log.debug("getting record from admin auth cache");
    return null;
  }
}
