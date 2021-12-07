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

package com.opsmx.spinnaker.gate.interceptor;

import com.google.gson.Gson;
import com.opsmx.spinnaker.gate.cache.DatasourceCaching;
import com.opsmx.spinnaker.gate.cache.OesCacheManager;
import com.opsmx.spinnaker.gate.enums.OesServices;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OesInterceptor implements Interceptor {

  private Gson gson = new Gson();

  @Autowired private OesCacheManager oesCacheManager;

  @Autowired private DatasourceCaching datasourceCaching;

  @Override
  public Response intercept(Chain chain) throws IOException {
    log.info("retrofit request interepted");
    Request request = chain.request();
    Response response = null;
    try {

      String path = request.url().url().getPath();
      String base = getBaseUrl(path);
      if (isOesService(base) && isRegisteredCachingEndpoint(path)) {
        CacheManager cacheManager = oesCacheManager.getConcurrentMapCacheManager();
        ConcurrentMapCache concurrentMapCache =
            (ConcurrentMapCache) cacheManager.getCache("datasource");
        if (isCacheEmpty(concurrentMapCache)) {
          response = chain.proceed(request);
          if (response.isSuccessful()) {
            log.info("username : {}", request.header("x-spinnaker-user"));
            Response finalResponse = response;
            Runnable runnable =
                () -> {
                  try {
                    handle(finalResponse, request.header("x-spinnaker-user"));
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                };
            runnable.run();
          }
        } else {
          log.info("username 2 : {}", request.header("x-spinnaker-user"));
          response =
              datasourceCaching.getResponse(request.header("x-spinnaker-user") + "-datasource");
          if (response == null) {
            response = chain.proceed(request);
            handle(response, request.header("x-spinnaker-user"));
          }
        }
      } else {
        response = chain.proceed(request);
      }
    } catch (Exception e) {
      log.error("Exception occurred while intercepting request : {}", e);
      Response finalResponse1 = response;
      Runnable runnable =
          () -> {
            try {
              handle(finalResponse1, request.header("x-spinnaker-user"));
            } catch (IOException ie) {
              ie.printStackTrace();
            }
          };
      runnable.run();
      response = chain.proceed(request);
    }
    log.info("response : {}", response);
    return response;
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

  private boolean isOesService(String baseUrl) {

    boolean flag = Boolean.FALSE;
    try {
      switch (OesServices.valueOf(baseUrl)) {
        case dashboardservice:
          flag = Boolean.TRUE;
          break;
      }
    } catch (Exception e) {
      log.debug("Not an OES service : {}", e.getMessage());
    }
    return flag;
  }

  private void handle(Response response, String userName) throws IOException {
    try {
      Thread.sleep(5000);
      // Response resp = datasourceCaching.cacheResponse(userName + "-datasource", response);
      String responseBody = parseResponse(response);
      log.info("response body : {}", responseBody);
      List<Map<String, Object>> datasources = gson.fromJson(responseBody, List.class);
      log.info("datasources : {}", datasources);
      //      datasources.forEach(
      //          datasource ->
      //              datasourceCaching.populateDatasourceCache(
      //                  userName + "-" + datasource.get("id"), datasource));
    } catch (Exception e) {
      log.error("Exception occurred while caching the response : {}", e);
    }
  }

  private boolean isRegisteredCachingEndpoint(String path) {
    boolean flag = Boolean.FALSE;
    switch (path) {
      case "/dashboardservice/v3/getAllDatasources":
        flag = Boolean.TRUE;
        break;
    }
    return flag;
  }

  private boolean isCacheEmpty(ConcurrentMapCache concurrentMapCache) {
    return concurrentMapCache.getNativeCache().isEmpty();
  }

  private String parseResponse(Response response) {
    try (InputStreamReader isr = new InputStreamReader(response.body().byteStream());
        BufferedReader br = new BufferedReader(isr)) {

      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
        sb.append("\n");
      }
      return sb.toString();
    } catch (IOException e) {
      log.error("Exception occurred while parsing the json : {}", e);
      return null;
    }
  }
}
