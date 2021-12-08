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
import com.opsmx.spinnaker.gate.cache.dashboard.DatasourceCaching;
import com.opsmx.spinnaker.gate.feignclient.DashboardClient;
import com.opsmx.spinnaker.gate.model.DatasourceRequestModel;
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
public class DatasourceCachingServiceImpl implements DashboardCachingService {

  private Gson gson = new Gson();

  @Autowired private OesCacheManager oesCacheManager;

  @Autowired private DatasourceCaching datasourceCaching;

  @Autowired private DashboardClient dashboardClient;

  @Override
  public void cacheResponse(Object response, String userName) {
    String responseBody = gson.toJson(response);
    List<Map<String, Object>> datasources = gson.fromJson(responseBody, List.class);
    log.debug("datasources : {}", datasources);
    datasources.forEach(
        datasource ->
            datasourceCaching.populateDatasourceCache(
                userName + "-" + datasource.get("id"), datasource));
  }

  @Override
  public boolean isCacheNotEmpty(String userName) {

    CacheManager cacheManager = oesCacheManager.getConcurrentMapCacheManager();
    ConcurrentMapCache concurrentMapCache =
        (ConcurrentMapCache) cacheManager.getCache("datasource");
    Set<Object> keySet = concurrentMapCache.getNativeCache().keySet();
    return keySet.stream()
        .filter(key -> ((String) key).trim().contains(userName))
        .findFirst()
        .isPresent();
  }

  @Override
  public Object fetchResponseFromCache(String userName) {

    CacheManager cacheManager = oesCacheManager.getConcurrentMapCacheManager();
    ConcurrentMapCache concurrentMapCache =
        (ConcurrentMapCache) cacheManager.getCache("datasource");
    Set<Object> keySet = concurrentMapCache.getNativeCache().keySet();
    Set<Object> filteredKeySet =
        keySet.stream()
            .filter(key -> ((String) key).trim().contains(userName))
            .collect(Collectors.toSet());

    return filteredKeySet.stream()
        .map(key -> concurrentMapCache.getNativeCache().get(key))
        .collect(Collectors.toList());
  }

  public void createDatasourceInCache(DatasourceRequestModel datasourceRequestModel) {

    Map<String, Object> datasource =
        dashboardClient
            .getDatasourceById(datasourceRequestModel.getId(), datasourceRequestModel.getUserName())
            .getBody();
    if (datasource != null && !datasource.isEmpty()) {
      datasourceCaching.populateDatasourceCache(
          datasourceRequestModel.getUserName() + "-" + datasourceRequestModel.getId(), datasource);
    }
  }

  public void evictRecordFromCache(DatasourceRequestModel datasourceRequestModel) {

    datasourceCaching.evictRecord(
        datasourceRequestModel.getUserName() + "-" + datasourceRequestModel.getId());
  }

  public Map<String, Object> getRecordFromCache(DatasourceRequestModel datasourceRequestModel) {

    return datasourceCaching.getRecord(
        datasourceRequestModel.getUserName() + "-" + datasourceRequestModel.getId());
  }
}
