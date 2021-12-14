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

package com.opsmx.spinnaker.gate.factory.platform;

import com.opsmx.spinnaker.gate.cache.Constants;
import com.opsmx.spinnaker.gate.service.AdminAuthService;
import com.opsmx.spinnaker.gate.service.PlatformCachingService;
import com.opsmx.spinnaker.gate.util.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlatformCachingServiceBeanFactory {

  @Autowired private AdminAuthService adminAuthService;

  public PlatformCachingService getBean(String path) {

    PlatformCachingService platformCachingService = null;
    path = CacheUtil.formatPath(path);
    switch (path) {
      case Constants.CHECK_IS_ADMIN_ENDPOINT:
        platformCachingService = adminAuthService;
        break;
    }
    return platformCachingService;
  }
}
