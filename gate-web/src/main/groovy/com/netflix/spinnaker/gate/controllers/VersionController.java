/*
 * Copyright 2018 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.gate.controllers;

import io.swagger.v3.oas.annotations.Operation;
import java.util.Optional;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version")
public class VersionController {

  @Operation(summary = "Fetch Gate's current version")
  @RequestMapping(method = RequestMethod.GET)
  Version getVersion() {
    return new Version();
  }

  @Data
  static class Version {

    private String version;

    public Version() {
      this.version =
          Optional.ofNullable(VersionController.class.getPackage().getImplementationVersion())
              .orElse("Unknown");
    }
  }
}
