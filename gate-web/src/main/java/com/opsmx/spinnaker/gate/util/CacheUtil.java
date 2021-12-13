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

package com.opsmx.spinnaker.gate.util;

import com.opsmx.spinnaker.gate.cache.Constants;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface CacheUtil {

  Logger logger = LoggerFactory.getLogger(CacheUtil.class);

  static boolean isRegisteredCachingEndpoint(String path) {
    boolean flag = Boolean.FALSE;
    path = formatPath(path);
    logger.info("formatted path : {}", path);
    switch (path) {
      case Constants.CHECK_IS_ADMIN_ENDPOINT:
        flag = Boolean.TRUE;
        break;

      case Constants.GET_ALL_DATASOURCES_ENDPOINT:
        flag = Boolean.TRUE;
        break;
    }
    return flag;
  }

  static String formatPath(String path) {
    String formattedPath = null;
    if (path != null) {
      List<String> pathVariables = Arrays.asList(path.split("/"));
      getRegisteredEndpoints()
          .forEach(
              endpoint -> {
                String[] paths = endpoint.split("/");
                if (pathVariables.size() == paths.length) {
                  for (int index = 0; index < paths.length; index++) {
                    String variable = paths[index];
                    if (variable.startsWith("{") && variable.endsWith("}")) {
                      pathVariables.set(index, variable);
                    }
                  }
                }
              });
      formattedPath = String.join("/", pathVariables);
    }
    return formattedPath;
  }

  static List<String> getRegisteredEndpoints() {

    List<String> registeredEndpoints = new ArrayList<>();
    registeredEndpoints.add(Constants.CHECK_IS_ADMIN_ENDPOINT);

    return registeredEndpoints;
  }
}
