/*
 * Copyright 2021 Netflix, Inc.
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
 *
 */

package com.netflix.spinnaker.gate.model


class ApprovalGateTriggerResponseModel {
  private Integer id
  private Integer approvalGateId
  private String activatedTime
  private String approvalCallbackURL
  private String rejectionCallbackURL

  static class ApprovalStatus{
      private String status

    String getStatus() {
      return status
    }

    void setStatus(String status) {
      this.status = status
    }
  }

  Integer getId() {
    return id
  }

  void setId(Integer id) {
    this.id = id
  }

  Integer getApprovalGateId() {
    return approvalGateId
  }

  void setApprovalGateId(Integer approvalGateId) {
    this.approvalGateId = approvalGateId
  }

  String getActivatedTime() {
    return activatedTime
  }

  void setActivatedTime(String activatedTime) {
    this.activatedTime = activatedTime
  }

  String getApprovalCallbackURL() {
    return approvalCallbackURL
  }

  void setApprovalCallbackURL(String approvalCallbackURL) {
    this.approvalCallbackURL = approvalCallbackURL
  }

  String getRejectionCallbackURL() {
    return rejectionCallbackURL
  }

  void setRejectionCallbackURL(String rejectionCallbackURL) {
    this.rejectionCallbackURL = rejectionCallbackURL
  }
}
