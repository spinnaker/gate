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

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class OesCacheManager {

  @Getter private CacheManager concurrentMapCacheManager;

  @Getter private CaffeineCacheManager caffeineCacheManager;

  @Value("${cache.expiryTime:600000}")
  private String cacheExpiryTimeout;

  @Primary
  @Bean(name = "concurrentMapCacheManager")
  public CacheManager concurrentMapCacheManager() {
    concurrentMapCacheManager = new ConcurrentMapCacheManager("datasource");
    return concurrentMapCacheManager;
  }

  @Bean(name = "caffeineCacheManager")
  public CacheManager cacheManager() {

    CaffeineCacheManager cacheManager = new CaffeineCacheManager("adminAuth");
    cacheManager.setCaffeine(
        Caffeine.newBuilder()
            .expireAfterWrite(Long.parseLong(cacheExpiryTimeout), TimeUnit.MILLISECONDS));
    caffeineCacheManager = cacheManager;
    return cacheManager;
  }
}
