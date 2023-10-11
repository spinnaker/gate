/*
 * Copyright 2022 OpsMx
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

package com.opsmx.spinnaker.gate.model;

import java.util.List;
import lombok.Data;

@Data
public class AuditData {
  private Source source;

  public AuditData(String name, List<String> roles, long timestamp) {
    this.source = new Source(name, roles, timestamp);
  }

  @Data
  public class Source {
    private String name;
    private Principal principal;
    private Long timestamp;

    public Source(String name, List<String> roles, long timestamp) {
      this.name = name;
      this.principal = new Principal(roles);
      this.timestamp = timestamp;
    }
  }

  @Data
  public class Principal {
    private List<String> roles;

    public Principal(List<String> roles) {
      this.roles = roles;
    }
  }
}
