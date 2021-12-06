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

package com.opsmx.spinnaker.gate.cache;

import java.util.Map;
import okhttp3.Response;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

public interface DatasourceCaching {

  @CachePut(
      value = "datasourceResponse",
      key = "#cacheKey",
      cacheManager = "concurrentMapCacheManager")
  Response cacheResponse(String cacheKey, Response response);

  @Cacheable(
      value = "datasourceResponse",
      key = "#cacheKey",
      cacheManager = "concurrentMapCacheManager")
  Response getResponse(String cacheKey);

  @CachePut(
      value = "datasource",
      key = "#datasourceKey",
      cacheManager = "concurrentMapCacheManager")
  Map<String, Object> populateDatasourceCache(String datasourceKey, Map<String, Object> datasource);
}
