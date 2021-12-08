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

package com.opsmx.spinnaker.gate.factory.dashboard;

import com.opsmx.spinnaker.gate.service.DashboardCachingService;
import com.opsmx.spinnaker.gate.service.DatasourceCachingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DashboardCachingServiceBeanFactory {

  @Autowired private DatasourceCachingServiceImpl datasourceCachingService;

  public DashboardCachingService getBean(String path) {

    DashboardCachingService dashboardCachingService = null;

    switch (path) {
      case "/dashboardservice/v3/getAllDatasources":
        dashboardCachingService = datasourceCachingService;
        break;
    }
    return dashboardCachingService;
  }
}
