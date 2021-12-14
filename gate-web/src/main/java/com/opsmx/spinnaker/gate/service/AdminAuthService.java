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

package com.opsmx.spinnaker.gate.service;

import com.google.gson.Gson;
import com.opsmx.spinnaker.gate.cache.OesCacheManager;
import com.opsmx.spinnaker.gate.cache.platform.AuthorizationCaching;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminAuthService implements PlatformCachingService {

  private Gson gson = new Gson();

  @Autowired private OesCacheManager oesCacheManager;

  @Autowired private AuthorizationCaching authorizationCaching;

  @Override
  public void cacheResponse(Object response, String userName) {

    String responseBody = gson.toJson(response);
    Map<String, Object> adminAuthResponse = gson.fromJson(responseBody, Map.class);
    authorizationCaching.populateAdminAuthCache(userName, adminAuthResponse);
  }

  @Override
  public boolean isCacheNotEmpty(String userName) {

    CacheManager cacheManager = oesCacheManager.getCaffeineCacheManager();
    CaffeineCache caffeineCache = (CaffeineCache) cacheManager.getCache("adminAuth");
    Set<Object> keySet = caffeineCache.getNativeCache().asMap().keySet();
    return keySet.stream()
        .filter(key -> ((String) key).trim().contains(userName))
        .findFirst()
        .isPresent();
  }

  @Override
  public Object fetchResponseFromCache(String userName) {
    return authorizationCaching.getRecordFromAdminAuthCache(userName);
  }
}
