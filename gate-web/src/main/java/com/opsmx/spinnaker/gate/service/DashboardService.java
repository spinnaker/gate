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
import com.opsmx.spinnaker.gate.cache.DatasourceCaching;
import com.opsmx.spinnaker.gate.cache.OesCacheManager;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DashboardService {

  private Gson gson = new Gson();

  @Autowired private OesCacheManager oesCacheManager;

  @Autowired private DatasourceCaching datasourceCaching;

  public void cacheResponse(Object response, String userName) {

    String responseBody = gson.toJson(response);
    log.info("response body : {}", responseBody);
    List<Map<String, Object>> datasources = gson.fromJson(responseBody, List.class);
    log.info("datasources : {}", datasources);
    datasources.forEach(
        datasource ->
            datasourceCaching.populateDatasourceCache(
                userName + "-" + datasource.get("id"), datasource));
  }

  public boolean isRegisteredCachingEndpoint(String path) {
    boolean flag = Boolean.FALSE;
    switch (path) {
      case "/dashboardservice/v3/getAllDatasources":
        flag = Boolean.TRUE;
        break;
    }
    return flag;
  }

  public boolean isCacheNotEmpty(String cacheName, String userName) {
    log.info(
        "checking if cache is empty for the cache name : {} and userName : {}",
        cacheName,
        userName);
    CacheManager cacheManager = oesCacheManager.getConcurrentMapCacheManager();
    ConcurrentMapCache concurrentMapCache = (ConcurrentMapCache) cacheManager.getCache(cacheName);
    Set<Object> keySet = concurrentMapCache.getNativeCache().keySet();
    return keySet.stream()
        .filter(key -> ((String) key).trim().contains(userName))
        .findFirst()
        .isPresent();
  }

  public Object fetchResponseFromCache(String cacheName, String userName) {

    CacheManager cacheManager = oesCacheManager.getConcurrentMapCacheManager();
    ConcurrentMapCache concurrentMapCache = (ConcurrentMapCache) cacheManager.getCache(cacheName);
    Set<Object> keySet = concurrentMapCache.getNativeCache().keySet();
    Set<Object> filteredKeySet =
        keySet.stream()
            .filter(key -> ((String) key).trim().contains(userName))
            .collect(Collectors.toSet());
    return filteredKeySet.stream()
        .map(key -> concurrentMapCache.getNativeCache().get(key))
        .collect(Collectors.toList());
  }
}
