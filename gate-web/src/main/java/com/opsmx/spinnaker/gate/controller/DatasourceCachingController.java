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

package com.opsmx.spinnaker.gate.controller;

import com.opsmx.spinnaker.gate.model.DatasourceRequestModel;
import com.opsmx.spinnaker.gate.service.DatasourceCachingServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/datasource/cache")
public class DatasourceCachingController {

  private DatasourceCachingServiceImpl datasourceCachingService;

  public DatasourceCachingController(DatasourceCachingServiceImpl datasourceCachingService) {
    this.datasourceCachingService = datasourceCachingService;
  }

  @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity createDatasourceInCache(
      @RequestBody DatasourceRequestModel datasourceRequestModel) {

    datasourceCachingService.createDatasourceInCache(datasourceRequestModel);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping(value = "/update")
  public ResponseEntity updateDatasourceInCache(
      @RequestBody DatasourceRequestModel datasourceRequestModel) {

    datasourceCachingService.createDatasourceInCache(datasourceRequestModel);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(value = "/evict")
  public ResponseEntity evictRecordFromCache(
      @RequestBody DatasourceRequestModel datasourceRequestModel) {

    datasourceCachingService.evictRecordFromCache(datasourceRequestModel);
    return ResponseEntity.noContent().build();
  }
}
