/*
 * Copyright 2022 OpsMx, Inc.
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

package com.opsmx.spinnaker.gate.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PermissionEnum {
  view("view a feature"),
  create_or_edit("create or edit a feature"),
  delete("delete a feature"),
  runtime_access("execute (trigger custom gate)"),
  approve_gate("approve a visibility gate");

  public String description;

  public String getDescription() {
    return this.description;
  }

  private PermissionEnum(String description) {
    this.description = description;
  }

  public static PermissionEnum getPermissionEnum(String permissionId) {
    return Arrays.stream(PermissionEnum.values())
        .filter(permission -> permission.name().equals(permissionId))
        .findFirst()
        .orElse(null);
  }

  public static List<PermissionEnum> getPermissionEnumsByValues(String[] values) {
    return Arrays.stream(values)
        .map(val -> PermissionEnum.getPermissionEnum(val))
        .collect(Collectors.toList());
  }

  public static String getPermissionEnumDisplayName(PermissionEnum permissionId) {
    String displayName = "";
    switch (permissionId) {
      case view:
        displayName = "View";
        break;
      case create_or_edit:
        displayName = "Create/Edit";
        break;
      case delete:
        displayName = "Delete";
        break;
      case runtime_access:
        displayName = "Runtime Access";
        break;
      case approve_gate:
        displayName = "Approval Gate";
        break;
    }
    return displayName;
  }
}
