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

package com.opsmx.spinnaker.gate.feignclient;

import com.opsmx.spinnaker.gate.cache.Constants;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "dashboardservice", url = "${services.dashboard.baseUrl}")
public interface DashboardClient {

  @GetMapping(
      value = Constants.GET_DATASOURCE_BY_ID_ENDPOINT,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Map<String, Object>> getDatasourceById(
      @PathVariable(value = "id") Integer id,
      @RequestHeader(name = "x-spinnaker-user") String userName);
}
