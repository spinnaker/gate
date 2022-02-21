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

package com.opsmx.spinnaker.gate.rbac;

import com.netflix.spinnaker.gate.model.PermissionModel;
import com.netflix.spinnaker.gate.services.OesAuthorizationService;
import com.opsmx.spinnaker.gate.enums.PermissionEnum;
import com.opsmx.spinnaker.gate.enums.RbacFeatureType;
import com.opsmx.spinnaker.gate.exception.AccessForbiddenException;
import com.opsmx.spinnaker.gate.exception.InvalidResourceIdException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnExpression("${rbac.enabled:false}")
public class ApplicationFeatureRbac {

  @Autowired private OesAuthorizationService oesAuthorizationService;

  private static final List<String> runtime_access = new ArrayList<>();
  public static final List<String> applicationFeatureRbacEndpoints = new ArrayList<>();
  public static final List<String> endpointsWithApplicationId = new ArrayList<>();
  public static final List<String> endpointsWithServiceId = new ArrayList<>();
  public static final List<String> endpointsWithPipelineId = new ArrayList<>();
  public static final List<String> endpointsWithGateId = new ArrayList<>();
  public static final List<String> endpointsWithApprovalGateId = new ArrayList<>();
  public static final List<String> endpointsWithApprovalGateInstanceId = new ArrayList<>();
  public static final List<String> endpointsWithApprovalPolicyId = new ArrayList<>();

  private static final String YOU_DO_NOT_HAVE = "You do not have : ";
  private static final String PERMISSION_FOR_THE_FEATURE_TYPE =
      " permission for the feature type : ";
  private static final String TO_PERFORM_THIS_OPERATION = " to perform this operation";

  static {
    populateDashboardServiceApis();
    populatePlatformServiceApis();
    populateVisibilityServiceApis();
    populateSaporServiceApis();
    populateAnalyticServiceApis();
  }

  public void authorizeUserForFeatureVisibility(String userName) {

    Boolean isFeatureVisibility;

    isFeatureVisibility =
        Boolean.parseBoolean(
            oesAuthorizationService
                .isFeatureVisibility(userName, RbacFeatureType.APP.name(), userName)
                .getBody()
                .get("isEnabled"));
    log.info("is feature visibility enabled : {}", isFeatureVisibility);
    if (!isFeatureVisibility) {
      throw new AccessForbiddenException(
          "You do not have permission for the feature type : " + RbacFeatureType.APP.description);
    }
  }

  public void authorizeUserForApplicationId(
      String username, String endpointUrl, String httpMethod) {

    HttpMethod method = HttpMethod.valueOf(httpMethod);
    Integer applicationId = getApplicationId(endpointUrl);
    PermissionModel permission;

    log.debug("authorizing the endpoint : {}", endpointUrl);

    switch (method) {
      case GET:
        permission =
            oesAuthorizationService
                .fetchPermissions(username, RbacFeatureType.APP.name(), applicationId, username)
                .getBody();
        log.info("permissions for the GET API : {}", permission);
        if (permission == null
            || !permission.getPermissions().contains(PermissionEnum.view.name())) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.view.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;

      case POST:
      case PUT:
        permission =
            oesAuthorizationService
                .fetchPermissions(username, RbacFeatureType.APP.name(), applicationId, username)
                .getBody();
        log.info("permissions for the POST or PUT API : {}", permission);
        if (permission == null
            || !permission.getPermissions().contains(PermissionEnum.create_or_edit.name())) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.create_or_edit.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;

      case DELETE:
        permission =
            oesAuthorizationService
                .fetchPermissions(username, RbacFeatureType.APP.name(), applicationId, username)
                .getBody();
        log.info("permissions for the DELETE API : {}", permission);
        if (permission == null
            || !permission.getPermissions().contains(PermissionEnum.delete.name())) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.delete.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;
    }
  }

  private Integer getApplicationId(String endpoint) {
    Integer applicationId = 0;
    List<String> pathComps = Arrays.asList(endpoint.split("/"));
    if (pathComps.contains("applications")) {
      int index = pathComps.indexOf("applications");
      applicationId = Integer.parseInt(pathComps.get(index + 1));
    } else if (pathComps.contains("application")) {
      int index = pathComps.indexOf("application");
      applicationId = Integer.parseInt(pathComps.get(index + 1));
    } else if (pathComps.contains("deleteApplication")) {
      int index = pathComps.indexOf("deleteApplication");
      applicationId = Integer.parseInt(pathComps.get(index + 1));
    } else if (pathComps.contains("template")) {
      int index = pathComps.indexOf("template");
      applicationId = Integer.parseInt(pathComps.get(index + 1));
    }

    if (applicationId == null || applicationId.equals(0)) {
      throw new InvalidResourceIdException("Invalid resource Id");
    }
    return applicationId;
  }

  public void authorizeUserForServiceId(String username, String endpointUrl, String httpMethod) {

    HttpMethod method = HttpMethod.valueOf(httpMethod);
    Integer serviceId = getServiceId(endpointUrl);
    Boolean isAuthorized;

    log.info("authorizing the endpoint for service Id : {}", endpointUrl);

    switch (method) {
      case GET:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.view.name(),
                        serviceId,
                        null,
                        null,
                        null,
                        null,
                        null,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info("is authorized for the service Id GET API: {}, {}", serviceId, isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.view.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;

      case POST:
      case PUT:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.create_or_edit.name(),
                        serviceId,
                        null,
                        null,
                        null,
                        null,
                        null,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info(
            "is authorized for the service Id POST or PUT API: {}, {}", serviceId, isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.create_or_edit.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;

      case DELETE:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.delete.name(),
                        serviceId,
                        null,
                        null,
                        null,
                        null,
                        null,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info("is authorized for the service Id DELETE API: {}, {}", serviceId, isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.delete.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;
    }
  }

  private Integer getServiceId(String endpoint) {
    Integer serviceId = 0;
    List<String> pathComps = Arrays.asList(endpoint.split("/"));
    if (pathComps.contains("services")) {
      int index = pathComps.indexOf("services");
      serviceId = Integer.parseInt(pathComps.get(index + 1));
    } else if (pathComps.contains("service")) {
      int index = pathComps.indexOf("service");
      serviceId = Integer.parseInt(pathComps.get(index + 1));
    }

    if (serviceId == null || serviceId.equals(0)) {
      throw new InvalidResourceIdException("Invalid resource Id");
    }
    return serviceId;
  }

  public void authorizeUserForPipelineId(String username, String endpointUrl, String httpMethod) {

    HttpMethod method = HttpMethod.valueOf(httpMethod);
    Integer pipelineId = getPipelineId(endpointUrl);
    Boolean isAuthorized;

    log.info("authorizing the endpoint : {}", endpointUrl);

    switch (method) {
      case GET:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.view.name(),
                        null,
                        pipelineId,
                        null,
                        null,
                        null,
                        null,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info("is authorized for the pipeline Id GET API: {}, {}", pipelineId, isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.view.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;

      case POST:
      case PUT:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.create_or_edit.name(),
                        null,
                        pipelineId,
                        null,
                        null,
                        null,
                        null,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info(
            "is authorized for the pipeline Id POST or PUT API: {}, {}", pipelineId, isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.create_or_edit.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;

      case DELETE:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.delete.name(),
                        null,
                        pipelineId,
                        null,
                        null,
                        null,
                        null,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info("is authorized for the pipeline Id DELETE API: {}, {}", pipelineId, isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.delete.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;
    }
  }

  private Integer getPipelineId(String endpoint) {
    Integer pipelineId = 0;
    List<String> pathComps = Arrays.asList(endpoint.split("/"));
    if (pathComps.contains("pipelines")) {
      int index = pathComps.indexOf("pipelines");
      pipelineId = Integer.parseInt(pathComps.get(index + 1));
    } else if (pathComps.contains("pipeline")) {
      int index = pathComps.indexOf("pipeline");
      pipelineId = Integer.parseInt(pathComps.get(index + 1));
    }

    if (pipelineId == null || pipelineId.equals(0)) {
      throw new InvalidResourceIdException("Invalid resource Id");
    }
    return pipelineId;
  }

  public void authorizeUserForGateId(String username, String endpointUrl, String httpMethod) {

    HttpMethod method = HttpMethod.valueOf(httpMethod);
    Integer gateId = getGateId(endpointUrl);
    Boolean isAuthorized;

    log.info("authorizing the endpoint : {}", endpointUrl);

    switch (method) {
      case GET:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.view.name(),
                        null,
                        null,
                        gateId,
                        null,
                        null,
                        null,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info("is authorized for the gate Id GET API: {}, {}", gateId, isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.view.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;

      case POST:
      case PUT:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.create_or_edit.name(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        gateId,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info("is authorized for the gate Id POST or PUT API: {}, {}", gateId, isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.create_or_edit.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;

      case DELETE:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.delete.name(),
                        null,
                        null,
                        gateId,
                        null,
                        null,
                        null,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info("is authorized for the gate Id DELETE API: {}, {}", gateId, isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.delete.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;
    }
  }

  private Integer getGateId(String endpoint) {
    Integer gateId = 0;
    List<String> pathComps = Arrays.asList(endpoint.split("/"));
    if (pathComps.contains("gates")) {
      int index = pathComps.indexOf("gates");
      gateId = Integer.parseInt(pathComps.get(index + 1));
    } else if (pathComps.contains("gate")) {
      int index = pathComps.indexOf("gate");
      gateId = Integer.parseInt(pathComps.get(index + 1));
    } else if (pathComps.contains("serviceGates")) {
      int index = pathComps.indexOf("serviceGates");
      gateId = Integer.parseInt(pathComps.get(index + 1));
    } else if (pathComps.contains("serviceGate")) {
      int index = pathComps.indexOf("serviceGate");
      gateId = Integer.parseInt(pathComps.get(index + 1));
    }

    if (gateId == null || gateId.equals(0)) {
      throw new InvalidResourceIdException("Invalid resource Id");
    }
    return gateId;
  }

  public void authorizeUserForApprovalGateId(
      String username, String endpointUrl, String httpMethod) {

    HttpMethod method = HttpMethod.valueOf(httpMethod);
    Integer approvalGateId = getApprovalGateId(endpointUrl);
    Boolean isAuthorized;

    log.info("authorizing the endpoint : {}", endpointUrl);

    switch (method) {
      case GET:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.view.name(),
                        null,
                        null,
                        null,
                        approvalGateId,
                        null,
                        null,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info(
            "is authorized for the approval gate Id GET API: {}, {}", approvalGateId, isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.view.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;

      case POST:
      case PUT:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.create_or_edit.name(),
                        null,
                        null,
                        null,
                        approvalGateId,
                        null,
                        null,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info(
            "is authorized for the approval gate Id POST or PUT API: {}, {}",
            approvalGateId,
            isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.create_or_edit.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;

      case DELETE:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.delete.name(),
                        null,
                        null,
                        null,
                        approvalGateId,
                        null,
                        null,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info(
            "is authorized for the approval gate Id DELETE API: {}, {}",
            approvalGateId,
            isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.delete.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;
    }
  }

  private Integer getApprovalGateId(String endpoint) {
    Integer approvalGateId = 0;
    List<String> pathComps = Arrays.asList(endpoint.split("/"));
    if (pathComps.contains("approvalGates")) {
      int index = pathComps.indexOf("approvalGates");
      approvalGateId = Integer.parseInt(pathComps.get(index + 1));
    }

    if (approvalGateId == null || approvalGateId.equals(0)) {
      throw new InvalidResourceIdException("Invalid resource Id");
    }
    return approvalGateId;
  }

  public void authorizeUserForApprovalGateInstanceId(
      String username, String endpointUrl, String httpMethod) {

    HttpMethod method = HttpMethod.valueOf(httpMethod);
    Integer approvalGateInstanceId = getApprovalGateInstanceId(endpointUrl);
    Boolean isAuthorized;

    log.info("authorizing the endpoint : {}", endpointUrl);

    switch (method) {
      case GET:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.view.name(),
                        null,
                        null,
                        null,
                        null,
                        approvalGateInstanceId,
                        null,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info(
            "is authorized for the approval gate instance Id GET API: {}, {}",
            approvalGateInstanceId,
            isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.view.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;

      case POST:
      case PUT:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.create_or_edit.name(),
                        null,
                        null,
                        null,
                        null,
                        approvalGateInstanceId,
                        null,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info(
            "is authorized for the approval gate instance Id POST or PUT API: {}, {}",
            approvalGateInstanceId,
            isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.create_or_edit.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;

      case DELETE:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.delete.name(),
                        null,
                        null,
                        null,
                        null,
                        approvalGateInstanceId,
                        null,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info(
            "is authorized for the approval gate instance Id DELETE API: {}, {}",
            approvalGateInstanceId,
            isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.delete.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;
    }
  }

  private Integer getApprovalGateInstanceId(String endpoint) {
    Integer approvalGateInstanceId = 0;
    List<String> pathComps = Arrays.asList(endpoint.split("/"));
    if (pathComps.contains("approvalGateInstances")) {
      int index = pathComps.indexOf("approvalGateInstances");
      approvalGateInstanceId = Integer.parseInt(pathComps.get(index + 1));
    }

    if (approvalGateInstanceId == null || approvalGateInstanceId.equals(0)) {
      throw new InvalidResourceIdException("Invalid resource Id");
    }
    return approvalGateInstanceId;
  }

  public void authorizeUserForApprovalPolicyId(
      String username, String endpointUrl, String httpMethod) {

    HttpMethod method = HttpMethod.valueOf(httpMethod);
    Integer approvalPolicyId = getApprovalPolicyId(endpointUrl);
    Boolean isAuthorized;

    log.info("authorizing the endpoint : {}", endpointUrl);

    switch (method) {
      case GET:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.view.name(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        approvalPolicyId,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info(
            "is authorized for the approval policy Id GET API: {}, {}",
            approvalPolicyId,
            isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.view.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;

      case POST:
      case PUT:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.create_or_edit.name(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        approvalPolicyId,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info(
            "is authorized for the approval policy Id POST or PUT API: {}, {}",
            approvalPolicyId,
            isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.create_or_edit.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;

      case DELETE:
        isAuthorized =
            Boolean.parseBoolean(
                oesAuthorizationService
                    .isAuthorizedUser(
                        username,
                        PermissionEnum.delete.name(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        approvalPolicyId,
                        username)
                    .getBody()
                    .get("isEnabled"));
        log.info(
            "is authorized for the approval policy Id DELETE API: {}, {}",
            approvalPolicyId,
            isAuthorized);
        if (isAuthorized == null || !isAuthorized) {
          throw new AccessForbiddenException(
              YOU_DO_NOT_HAVE
                  + PermissionEnum.delete.name()
                  + PERMISSION_FOR_THE_FEATURE_TYPE
                  + RbacFeatureType.APP.description
                  + TO_PERFORM_THIS_OPERATION);
        }
        break;
    }
  }

  private Integer getApprovalPolicyId(String endpoint) {
    Integer approvalGateInstanceId = 0;
    List<String> pathComps = Arrays.asList(endpoint.split("/"));
    if (pathComps.contains("policy")) {
      int index = pathComps.indexOf("policy");
      approvalGateInstanceId = Integer.parseInt(pathComps.get(index + 1));
    }

    if (approvalGateInstanceId == null || approvalGateInstanceId.equals(0)) {
      throw new InvalidResourceIdException("Invalid resource Id");
    }
    return approvalGateInstanceId;
  }

  private static void populateDashboardServiceApis() {

    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v2/applications/{applicationId}/service/{serviceId}");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v2/applications/{applicationId}/service");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v2/autopilot/service/feature/configuration");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v2/applications/{applicationId}/service/{serviceId}");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v2/autopilot/service/{serviceId}/application/{applicationId}/feature/configuration");
    applicationFeatureRbacEndpoints.add("/dashboardservice/v2/sapor/service/feature/configuration");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v2/sapor/service/{serviceId}/application/{applicationId}/feature/configuration");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v2/visibilityservice/service/feature/configuration");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v2/visibilityservice/service/{serviceId}/feature/configuration/{approvalGateId}");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v2/dashboardservice/{username}/applications");
    applicationFeatureRbacEndpoints.add("/dashboardservice/v2/applications/{applicationId}");
    applicationFeatureRbacEndpoints.add("/dashboardservice/v2/application");
    applicationFeatureRbacEndpoints.add("/dashboardservice/v2/application/{applicationId}");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v2/users/{username}/applications/latest-canary");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v2/applications/{applicationId}/usergroups/permissions");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v1/dashboardservice/{username}/applications");
    applicationFeatureRbacEndpoints.add("/dashboardservice/v1/applications/{applicationId}");
    applicationFeatureRbacEndpoints.add("/dashboardservice/v1/application");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v1/users/{username}/applications/latest-canary");
    applicationFeatureRbacEndpoints.add("/dashboardservice/v1/application/{applicationId}");
    applicationFeatureRbacEndpoints.add("/dashboardservice/v3/pipelines/{pipelineId}/gates");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v3/pipelines/{pipelineId}/gates/{gateId}");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v3/applications/{applicationId}/services");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v3/pipelines/{pipelineId}/gates/{gateId}/references/{refId}");
    applicationFeatureRbacEndpoints.add("/dashboardservice/v3/applications/{applicationId}");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v3/applications/{applicationId}/service/{serviceId}");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v4/applications/{applicationId}/deployments/history");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v4/applications/{applicationId}/services/{serviceId}/deployments/current");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v4/applications/{applicationId}/services/{serviceId}/deployments/history");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v4/applications/{applicationId}/services/{serviceId}/deploymentCurrents/{deploymentCurrentId}/gates");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v4/applications/{applicationId}/services/{serviceId}/deploymentHistory/{deploymentHistoryId}/gates");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v4/applications/{applicationId}/deploymentHistory/{deploymentHistoryId}/gates");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v4/applications/{applicationId}/services/{serviceId}/deployments");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v4/applications/{applicationId}/deployments");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v4/applications/{applicationId}/environments/{environmentName}");
    applicationFeatureRbacEndpoints.add("/dashboardservice/v4/users/{username}/summary");
    applicationFeatureRbacEndpoints.add("/dashboardservice/v4/pipelines/{pipelineId}/gates");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v4/pipelines/{pipelineId}/gates/{gateId}");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v4/applications/{applicationId}/services");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v4/pipelines/{pipelineId}/gates/{gateId}/references/{refId}");
    applicationFeatureRbacEndpoints.add("/dashboardservice/v4/users/{username}/applications");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v1/applications/service/{applicationId}");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v2/applications/{applicationId}/pending_approvals");
    applicationFeatureRbacEndpoints.add("/dashboardservice/v2/services/{serviceId}/gates");
    applicationFeatureRbacEndpoints.add("/dashboardservice/v2/services/{serviceId}/gates/{id}");
    applicationFeatureRbacEndpoints.add(
        "/dashboardservice/v2/applications/{applicationId}/services}");
    applicationFeatureRbacEndpoints.add("/dashboardservice/v2/{userId}/applications");
    applicationFeatureRbacEndpoints.add("/dashboardservice/v2/users/{username}/applications");

    endpointsWithApplicationId.add(
        "/dashboardservice/v2/applications/{applicationId}/service/{serviceId}");
    endpointsWithApplicationId.add("/dashboardservice/v2/applications/{applicationId}/service");
    endpointsWithApplicationId.add(
        "/dashboardservice/v2/applications/{applicationId}/service/{serviceId}");
    endpointsWithApplicationId.add(
        "/dashboardservice/v2/autopilot/service/{serviceId}/application/{applicationId}/feature/configuration");
    endpointsWithApplicationId.add(
        "/dashboardservice/v2/sapor/service/{serviceId}/application/{applicationId}/feature/configuration");
    endpointsWithApplicationId.add("/dashboardservice/v2/applications/{applicationId}");
    endpointsWithApplicationId.add("/dashboardservice/v2/application/{applicationId}");
    endpointsWithApplicationId.add(
        "/dashboardservice/v2/applications/{applicationId}/usergroups/permissions");
    endpointsWithApplicationId.add("/dashboardservice/v1/applications/{applicationId}");
    endpointsWithApplicationId.add("/dashboardservice/v1/application/{applicationId}");
    endpointsWithApplicationId.add("/dashboardservice/v3/applications/{applicationId}/services");
    endpointsWithApplicationId.add("/dashboardservice/v3/applications/{applicationId}");
    endpointsWithApplicationId.add(
        "/dashboardservice/v3/applications/{applicationId}/service/{serviceId}");
    endpointsWithApplicationId.add(
        "/dashboardservice/v4/applications/{applicationId}/deployments/history");
    endpointsWithApplicationId.add(
        "/dashboardservice/v4/applications/{applicationId}/services/{serviceId}/deployments/current");
    endpointsWithApplicationId.add(
        "/dashboardservice/v4/applications/{applicationId}/services/{serviceId}/deployments/history");
    endpointsWithApplicationId.add(
        "/dashboardservice/v4/applications/{applicationId}/services/{serviceId}/deploymentCurrents/{deploymentCurrentId}/gates");
    endpointsWithApplicationId.add(
        "/dashboardservice/v4/applications/{applicationId}/services/{serviceId}/deploymentHistory/{deploymentHistoryId}/gates");
    endpointsWithApplicationId.add(
        "/dashboardservice/v4/applications/{applicationId}/deploymentHistory/{deploymentHistoryId}/gates");
    endpointsWithApplicationId.add(
        "/dashboardservice/v4/applications/{applicationId}/services/{serviceId}/deployments");
    endpointsWithApplicationId.add("/dashboardservice/v4/applications/{applicationId}/deployments");
    endpointsWithApplicationId.add(
        "/dashboardservice/v4/applications/{applicationId}/environments/{environmentName}");
    endpointsWithApplicationId.add("/dashboardservice/v4/applications/{applicationId}/services");
    endpointsWithApplicationId.add("/dashboardservice/v1/applications/service/{applicationId}");
    endpointsWithApplicationId.add(
        "/dashboardservice/v2/applications/{applicationId}/pending_approvals");
    endpointsWithApplicationId.add("/dashboardservice/v2/applications/{applicationId}/services");
    endpointsWithApplicationId.add("/dashboardservice/v2/applications/{applicationId}");

    endpointsWithServiceId.add(
        "/dashboardservice/v2/visibilityservice/service/{serviceId}/feature/configuration/{approvalGateId}");
    endpointsWithServiceId.add("/dashboardservice/v2/services/{serviceId}/gates");
    endpointsWithServiceId.add("/dashboardservice/v2/services/{serviceId}/gates/{id}");

    endpointsWithPipelineId.add("/dashboardservice/v3/pipelines/{pipelineId}/gates");
    endpointsWithPipelineId.add("/dashboardservice/v3/pipelines/{pipelineId}/gates/{gateId}");
    endpointsWithPipelineId.add(
        "/dashboardservice/v3/pipelines/{pipelineId}/gates/{gateId}/references/{refId}");
    endpointsWithPipelineId.add("/dashboardservice/v4/pipelines/{pipelineId}/gates");
    endpointsWithPipelineId.add("/dashboardservice/v4/pipelines/{pipelineId}/gates/{gateId}");
    endpointsWithPipelineId.add(
        "/dashboardservice/v4/pipelines/{pipelineId}/gates/{gateId}/references/{refId}");
  }

  private static void populatePlatformServiceApis() {

    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/deploymentsCurrent/pipelines");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/deploymentsHistory/pipelines");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/pipeline/latest");
    applicationFeatureRbacEndpoints.add("/platformservice/v4/gate/executions/track");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/gate/executions/pipelines/{pipelineId}/{executionId}/track");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/deployments/applications/{applicationName}/pipelines/uuid/{pipelineUuid}/name/{pipelineName}/executions/{executionId}");
    applicationFeatureRbacEndpoints.add("/platformservice/v4/deployments/pipelinetype");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/applications/{applicationId}/deployments/history");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/deployments/current");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/deployments/history");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/deployments/gates");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/service/deploymentCurrents/{deploymentCurrentId}");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/deployments/count/applications/{applicationId}/services/{serviceId}");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/deployments/count/applications/{applicationId}");
    applicationFeatureRbacEndpoints.add("/platformservice/v4/deployments/count/applications");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/service/deploymentHistory/{deploymentHistoryId}");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/pipelines/{pipelineId}/deploymentsHistory/gates");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/applications/{applicationId}/deploymentsHistory/gates");
    applicationFeatureRbacEndpoints.add("/platformservice/v4/pipeline/executions/track");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/pipeline/executions/{executionId}/track");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/deployments/pipelines");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v1/applications/{applicationId}/service/{id}");
    applicationFeatureRbacEndpoints.add("/platformservice/v1/applications/{applicationId}/service");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v1/applications/{applicationId}/service/{serviceId}");
    applicationFeatureRbacEndpoints.add("/platformservice/v1/service/feature");
    applicationFeatureRbacEndpoints.add("/platformservice/v1/services/{serviceId}/features");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v1/applications/{applicationId}/service/{serviceId}");
    applicationFeatureRbacEndpoints.add("/platformservice/v1/services/{serviceId}/features");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v1/users/{username}/applications/{featureType}");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v1/services/{serviceId}/features/{feature}");
    applicationFeatureRbacEndpoints.add("/platformservice/v3/applications/{applicationId}");
    applicationFeatureRbacEndpoints.add("/platformservice/v3/applications");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v3/applications/{applicationId}/services/count");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v3/applications/{applicationId}/service/{serviceId}");
    applicationFeatureRbacEndpoints.add("/platformservice/v3/applications/{applicationId}/service");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v3/applications/{applicationId}/service/{serviceId}");
    applicationFeatureRbacEndpoints.add("/platformservice/v3/service/{serviceId}/pipelinemap");
    applicationFeatureRbacEndpoints.add("/platformservice/v3/service/{serviceId}/setdisplay");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v1/applications/{applicationId}/usergroups/permissions");
    applicationFeatureRbacEndpoints.add("/platformservice/v1/users/{username}/applications");
    applicationFeatureRbacEndpoints.add("/platformservice/v1/users/{username}/applicationscount");
    applicationFeatureRbacEndpoints.add("/platformservice/v1/users/{username}/services");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v1/users/{username}/verifications/applications");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v1/applications/{applicationName}/does-exist");
    applicationFeatureRbacEndpoints.add("/platformservice/v1/applications/{applicationId}");
    applicationFeatureRbacEndpoints.add("/platformservice/v1/applications/{applicationId}");
    applicationFeatureRbacEndpoints.add("/platformservice/v1/applications");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/applications/{applicationId}/environments/{environmentName}");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/environment");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/applications/{applicationId}/environment");
    applicationFeatureRbacEndpoints.add("/platformservice/v2/applications/name/{applicationName}");
    applicationFeatureRbacEndpoints.add("/platformservice/v2/services/{serviceId}/gates");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v2/applications/{applicationId}/environments");
    applicationFeatureRbacEndpoints.add("/platformservice/v2/services/{serviceId}/gates/{id}");
    applicationFeatureRbacEndpoints.add("/platformservice/v2/applications/{applicationId}");
    applicationFeatureRbacEndpoints.add("/platformservice/v2/gates/{gateId}");
    applicationFeatureRbacEndpoints.add("/platformservice/v2/services/{serviceId}/pipeline");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v2/services/{serviceId}/gates/{id}/references/{refId}");
    applicationFeatureRbacEndpoints.add("/platformservice/v2/applications");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v2/applications/{applicationId}/services");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v2/applications/{applicationId}/environments");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v2/services/{serviceId}/pipeline/refresh");
    applicationFeatureRbacEndpoints.add("/platformservice/v2/spinnaker/stage/service/{serviceId}");
    applicationFeatureRbacEndpoints.add("/platformservice/v2/services/gates");
    applicationFeatureRbacEndpoints.add("/platformservice/v2/users/{username}/applications");
    applicationFeatureRbacEndpoints.add("/platformservice/v3/services/{serviceId}/gates");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v3/pipelines/{pipelineId}/gates/{gateId}");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v3/applications/{applicationId}/pipelines");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v3/pipelines/{pipelineId}/gates/{gateId}/references/{refId}");
    applicationFeatureRbacEndpoints.add("/platformservice/v3/pipelines/{id}");
    applicationFeatureRbacEndpoints.add("/platformservice/v3/pipelines/{pipelineId}/gates");
    applicationFeatureRbacEndpoints.add("/platformservice/v3/services/{serviceId}/pipelines");
    applicationFeatureRbacEndpoints.add("/platformservice/v3/pipelineStages/{id}");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v3/applications/{applicationId}/services");
    applicationFeatureRbacEndpoints.add("/platformservice/v3/pipelines/{pipelineId}/services");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v3/applications/{applicationId}/services/pipelines");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v3/pipelines/{pipelineId}/gates/{gateId}/references/{refId}");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v3/environments/order/services/{serviceId}");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v3/applications/{applicationId}/service/{serviceId}/pipelines");
    applicationFeatureRbacEndpoints.add("/platformservice/v3/pipelines");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v3/spinnaker/stage/pipelines/{pipelineId}");
    applicationFeatureRbacEndpoints.add("/platformservice/v3/services/gates");
    applicationFeatureRbacEndpoints.add("/platformservice/v3/applications/pipelines");
    applicationFeatureRbacEndpoints.add("/platformservice/v3/users/{username}/services");
    applicationFeatureRbacEndpoints.add("/platformservice/v3/pipelines/{pipelineId}/view");
    applicationFeatureRbacEndpoints.add("/platformservice/v4/latest/applications/{applicationId}");
    applicationFeatureRbacEndpoints.add("/platformservice/v4/gates/{gateId}");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/pipelines/{pipelineId}/gates/{gateId}/references/{refId}");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/pipelines/uuid/{pipelineUuid}/name/{pipelineName}/applications/{applicationName}");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/spinnaker/stage/pipelines/{pipelineId}");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/pipelines/uuid/{pipelineUuid}/name/{pipelineName}/gates/references/{refId}/type/{gateType}/applications/{applicationName}");
    applicationFeatureRbacEndpoints.add("/platformservice/v4/pipelines/{pipelineId}/gates");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/pipelines/{pipelineName}/uuid/{pipelineUuid}/applications/{applicationName}");
    applicationFeatureRbacEndpoints.add("/platformservice/v4/pipeline");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/pipelines/{pipelineName}/applications/{applicationName}");
    applicationFeatureRbacEndpoints.add("/platformservice/v4/services/gates");
    applicationFeatureRbacEndpoints.add("/platformservice/v4/pipelines/{pipelineId}/view");
    applicationFeatureRbacEndpoints.add("/platformservice/v4/applications/{applicationId}");
    applicationFeatureRbacEndpoints.add(
        "/platformservice/v4/users/{username}/approvalgates/applications");

    endpointsWithApplicationId.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/deploymentsCurrent/pipelines");
    endpointsWithApplicationId.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/deploymentsHistory/pipelines");
    endpointsWithApplicationId.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/pipeline/latest");
    endpointsWithApplicationId.add(
        "/platformservice/v4/applications/{applicationId}/deployments/history");
    endpointsWithApplicationId.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/deployments/current");
    endpointsWithApplicationId.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/deployments/history");
    endpointsWithApplicationId.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/deployments/gates");
    endpointsWithApplicationId.add(
        "/platformservice/v4/deployments/count/applications/{applicationId}/services/{serviceId}");
    endpointsWithApplicationId.add(
        "/platformservice/v4/deployments/count/applications/{applicationId}");
    endpointsWithApplicationId.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/pipelines/{pipelineId}/deploymentsHistory/gates");
    endpointsWithApplicationId.add(
        "/platformservice/v4/applications/{applicationId}/deploymentsHistory/gates");
    endpointsWithApplicationId.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/deployments/pipelines");
    endpointsWithApplicationId.add("/platformservice/v1/applications/{applicationId}/service/{id}");
    endpointsWithApplicationId.add("/platformservice/v1/applications/{applicationId}/service");
    endpointsWithApplicationId.add(
        "/platformservice/v1/applications/{applicationId}/service/{serviceId}");
    endpointsWithApplicationId.add("/platformservice/v3/applications/{applicationId}");
    endpointsWithApplicationId.add(
        "/platformservice/v3/applications/{applicationId}/services/count");
    endpointsWithApplicationId.add(
        "/platformservice/v3/applications/{applicationId}/service/{serviceId}");
    endpointsWithApplicationId.add("/platformservice/v3/applications/{applicationId}/service");
    endpointsWithApplicationId.add("/platformservice/v1/applications/{applicationId}");
    endpointsWithApplicationId.add(
        "/platformservice/v4/applications/{applicationId}/environments/{environmentName}");
    endpointsWithApplicationId.add(
        "/platformservice/v4/applications/{applicationId}/services/{serviceId}/environment");
    endpointsWithApplicationId.add("/platformservice/v4/applications/{applicationId}/environment");
    endpointsWithApplicationId.add("/platformservice/v2/applications/{applicationId}/environments");
    endpointsWithApplicationId.add("/platformservice/v2/applications/{applicationId}");
    endpointsWithApplicationId.add("/platformservice/v2/applications/{applicationId}/services");
    endpointsWithApplicationId.add("/platformservice/v3/applications/{applicationId}/pipelines");
    endpointsWithApplicationId.add("/platformservice/v3/applications/{applicationId}/services");
    endpointsWithApplicationId.add(
        "/platformservice/v3/applications/{applicationId}/services/pipelines");
    endpointsWithApplicationId.add(
        "/platformservice/v3/applications/{applicationId}/service/{serviceId}/pipelines");
    endpointsWithApplicationId.add("/platformservice/v4/latest/applications/{applicationId}");
    endpointsWithApplicationId.add("/platformservice/v4/applications/{applicationId}");

    endpointsWithServiceId.add("/platformservice/v1/services/{serviceId}/features");
    endpointsWithServiceId.add("/platformservice/v1/services/{serviceId}/features/{feature}");
    endpointsWithServiceId.add("/platformservice/v3/service/{serviceId}/pipelinemap");
    endpointsWithServiceId.add("/platformservice/v3/service/{serviceId}/setdisplay");
    endpointsWithServiceId.add("/platformservice/v2/services/{serviceId}/gates");
    endpointsWithServiceId.add("/platformservice/v2/services/{serviceId}/gates/{id}");
    endpointsWithServiceId.add("/platformservice/v2/services/{serviceId}/pipeline");
    endpointsWithServiceId.add(
        "/platformservice/v2/services/{serviceId}/gates/{id}/references/{refId}");
    endpointsWithServiceId.add("/platformservice/v2/services/{serviceId}/pipeline/refresh");
    endpointsWithServiceId.add("/platformservice/v2/spinnaker/stage/service/{serviceId}");
    endpointsWithServiceId.add("/platformservice/v3/services/{serviceId}/gates");
    endpointsWithServiceId.add("/platformservice/v3/services/{serviceId}/pipelines");
    endpointsWithServiceId.add("/platformservice/v3/environments/order/services/{serviceId}");

    endpointsWithPipelineId.add(
        "/platformservice/v4/gate/executions/pipelines/{pipelineId}/{executionId}/track");
    endpointsWithPipelineId.add("/platformservice/v3/pipelines/{pipelineId}/gates/{gateId}");
    endpointsWithPipelineId.add(
        "/platformservice/v3/pipelines/{pipelineId}/gates/{gateId}/references/{refId}");
    endpointsWithPipelineId.add("/platformservice/v3/pipelines/{id}");
    endpointsWithPipelineId.add("/platformservice/v3/pipelines/{pipelineId}/gates");
    endpointsWithPipelineId.add("/platformservice/v3/pipelines/{pipelineId}/services");
    endpointsWithPipelineId.add("/platformservice/v3/spinnaker/stage/pipelines/{pipelineId}");
    endpointsWithPipelineId.add("/platformservice/v3/pipelines/{pipelineId}/view");
    endpointsWithPipelineId.add(
        "/platformservice/v4/pipelines/{pipelineId}/gates/{gateId}/references/{refId}");
    endpointsWithPipelineId.add("/platformservice/v4/spinnaker/stage/pipelines/{pipelineId}");
    endpointsWithPipelineId.add("/platformservice/v4/pipelines/{pipelineId}/gates");
    endpointsWithPipelineId.add("/platformservice/v4/pipelines/{pipelineId}/view");

    endpointsWithGateId.add("/platformservice/v2/gates/{gateId}");
    endpointsWithGateId.add("/platformservice/v4/gates/{gateId}");
  }

  private static void populateVisibilityServiceApis() {

    applicationFeatureRbacEndpoints.add("/visibilityservice/v3/serviceGates/{serviceGateId}");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v3/applications/{applicationId}/approvalGateInstances/activeCount");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v3/pipelines/{pipelineId}/approvalGates");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v3/applications/{applicationId}/approvalGates/{approvalGateId}/approvalGateInstances/latest");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v3/applications/{applicationId}/approvalGateInstances/latest");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v3/applications/{applicationId}/services/{serviceId}/approvalGateInstances/activeCount");
    applicationFeatureRbacEndpoints.add("/visibilityservice/v2/approvalGateInstances/{id}/status");
    applicationFeatureRbacEndpoints.add("/visibilityservice/v1/approvalGates/summary/count");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v1/approvalGates/{id}/toolTemplates/{templateId}/connector");
    applicationFeatureRbacEndpoints.add("/visibilityservice/v1/approvalGates/{id}/instances");
    applicationFeatureRbacEndpoints.add("/visibilityservice/v1/approvalGates/summary");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v1/approvalGates/{id}/templates/toolConnectors");
    applicationFeatureRbacEndpoints.add("/visibilityservice/v1/approvalGates");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v1/approvalGates/{id}/configuredtoolConnectors");
    applicationFeatureRbacEndpoints.add("/visibilityservice/v1/approvalGates/{id}/toolconnectors");
    applicationFeatureRbacEndpoints.add("/visibilityservice/v1/approvalGates/{id}");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v4/applications/{applicationId}/approvalGateInstances/latest");
    applicationFeatureRbacEndpoints.add("/visibilityservice/v5/policy/{policyId}/approvalGates");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v2/users/{username}/approvalGateInstances/{id}");
    applicationFeatureRbacEndpoints.add("/visibilityservice/v2/approvalGates");
    applicationFeatureRbacEndpoints.add("/visibilityservice/v2/approvalGates/{approvalGateId}");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v2/applications/{applicationId}/approvalGateInstances/latest");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v2/applications/{applicationId}/approvalGates/{approvalGateId}/approvalGateInstances/latest");
    applicationFeatureRbacEndpoints.add("/visibilityservice/v2/approvalGates/{approvalGateId}");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v2/users/{userid}/approvalGates/applications");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v2/serviceGates/{serviceGateId}/status");
    applicationFeatureRbacEndpoints.add("/visibilityservice/v2/services/{serviceId}/approvalGates");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v1/approvalGateParameter/{connectorType}/{approvalGateParameterId}");
    applicationFeatureRbacEndpoints.add("/visibilityservice/v1/approvalGateInstances/{id}");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v1/approvalGateInstances/{id}/toolConnectors/{connectorType}/visibilityserviceData");
    applicationFeatureRbacEndpoints.add("/visibilityservice/v1/approvalGateInstances/{id}/review");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v1/approvalGateInstances/{id}/customConnectors");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v1/approvalGateInstances/{approvalGateInstanceId}/customConnectors/{id}/visibilityserviceData");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v1/toolConnectors/{connectorType}/visibilityserviceData/deployments");
    applicationFeatureRbacEndpoints.add("/visibilityservice/v1/approvalData/deployments");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v1/users/{userid}/approvalGateInstances/activeCount");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v1/users/{userid}/approvalGates/applications");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v1/applications/{applicationId}/approvalGateInstances/latest");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v1/applications/{applicationId}/approvalGateInstances/activeCount");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v1/applications/{applicationId}/approvalGates");
    applicationFeatureRbacEndpoints.add(
        "/visibilityservice/v3/applications/{applicationId}/approvalGates}");

    endpointsWithApplicationId.add(
        "/visibilityservice/v3/applications/{applicationId}/approvalGateInstances/activeCount");
    endpointsWithApplicationId.add(
        "/visibilityservice/v3/applications/{applicationId}/approvalGates/{approvalGateId}/approvalGateInstances/latest");
    endpointsWithApplicationId.add(
        "/visibilityservice/v3/applications/{applicationId}/approvalGateInstances/latest");
    endpointsWithApplicationId.add(
        "/visibilityservice/v3/applications/{applicationId}/services/{serviceId}/approvalGateInstances/activeCount");
    endpointsWithApplicationId.add(
        "/visibilityservice/v4/applications/{applicationId}/approvalGateInstances/latest");
    endpointsWithApplicationId.add(
        "/visibilityservice/v2/applications/{applicationId}/approvalGateInstances/latest");
    endpointsWithApplicationId.add(
        "/visibilityservice/v2/applications/{applicationId}/approvalGates/{approvalGateId}/approvalGateInstances/latest");
    endpointsWithApplicationId.add(
        "/visibilityservice/v1/applications/{applicationId}/approvalGateInstances/latest");
    endpointsWithApplicationId.add(
        "/visibilityservice/v1/applications/{applicationId}/approvalGateInstances/activeCount");
    endpointsWithApplicationId.add(
        "/visibilityservice/v1/applications/{applicationId}/approvalGates");
    endpointsWithApplicationId.add(
        "/visibilityservice/v3/applications/{applicationId}/approvalGates");

    endpointsWithServiceId.add("/visibilityservice/v2/services/{serviceId}/approvalGates");

    endpointsWithPipelineId.add("/visibilityservice/v3/pipelines/{pipelineId}/approvalGates");

    endpointsWithGateId.add("/visibilityservice/v3/serviceGates/{serviceGateId}");
    endpointsWithGateId.add("/visibilityservice/v2/serviceGates/{serviceGateId}/status");

    endpointsWithApprovalGateId.add(
        "/visibilityservice/v1/approvalGates/{id}/toolTemplates/{templateId}/connector");
    endpointsWithApprovalGateId.add("/visibilityservice/v1/approvalGates/{id}/instances");
    endpointsWithApprovalGateId.add(
        "/visibilityservice/v1/approvalGates/{id}/templates/toolConnectors");
    endpointsWithApprovalGateId.add(
        "/visibilityservice/v1/approvalGates/{id}/configuredtoolConnectors");
    endpointsWithApprovalGateId.add("/visibilityservice/v1/approvalGates/{id}/toolconnectors");
    endpointsWithApprovalGateId.add("/visibilityservice/v1/approvalGates/{id}/toolConnectors");
    endpointsWithApprovalGateId.add("/visibilityservice/v1/approvalGates/{id}");
    endpointsWithApprovalGateId.add("/visibilityservice/v2/approvalGates/{approvalGateId}");

    endpointsWithApprovalGateInstanceId.add(
        "/visibilityservice/v2/approvalGateInstances/{id}/status");
    endpointsWithApprovalGateInstanceId.add(
        "/visibilityservice/v1/approvalGateInstances/{id}/status");
    endpointsWithApprovalGateInstanceId.add("/visibilityservice/v1/approvalGateInstances/{id}");
    endpointsWithApprovalGateInstanceId.add(
        "/visibilityservice/v1/approvalGateInstances/{id}/toolConnectors/{connectorType}/visibilityserviceData");
    endpointsWithApprovalGateInstanceId.add(
        "/visibilityservice/v1/approvalGateInstances/{id}/review");
    endpointsWithApprovalGateInstanceId.add(
        "/visibilityservice/v1/approvalGateInstances/{id}/customConnectors");
    endpointsWithApprovalGateInstanceId.add(
        "/visibilityservice/v1/approvalGateInstances/{approvalGateInstanceId}/customConnectors/{id}/visibilityserviceData");
    endpointsWithApprovalGateInstanceId.add(
        "/visibilityservice/v2/users/{username}/approvalGateInstances/{id}");

    endpointsWithApprovalPolicyId.add("/visibilityservice/v5/policy/{policyId}/approvalGates");
  }

  private static void populateSaporServiceApis() {

    applicationFeatureRbacEndpoints.add("/oes/appOnboarding/deleteApplication/{applicationId}");
    applicationFeatureRbacEndpoints.add(
        "/oes/appOnboarding/applications/{applicationId}/environments");
    applicationFeatureRbacEndpoints.add(
        "/oes/appOnboarding/applications/{applicationId}/imagesource");
    applicationFeatureRbacEndpoints.add(
        "/oes/appOnboarding/applications/{applicationId}/services/{serviceId}");
    applicationFeatureRbacEndpoints.add("/oes/appOnboarding/applications/{applicationId}");
    applicationFeatureRbacEndpoints.add("/oes/appOnboarding/spinnaker/applications");
    applicationFeatureRbacEndpoints.add("/oes/appOnboarding/spinnaker/{application}/pipeline");
    applicationFeatureRbacEndpoints.add("/oes/appOnboarding/spinnaker/pipeline/stage");
    applicationFeatureRbacEndpoints.add(
        "/oes/appOnboarding/spinnaker/{application}/pipeline/{pipelineName}");
    applicationFeatureRbacEndpoints.add(
        "/oes/policy/violations/applications/{applicationName}/services/{serviceId}");
    applicationFeatureRbacEndpoints.add("/oes/policy/gate");
    applicationFeatureRbacEndpoints.add(
        "/oes/policy/gate/{gateId}/executions/{executionId}/status");
    applicationFeatureRbacEndpoints.add("/oes/policy/gate/{gateId}");
    applicationFeatureRbacEndpoints.add(
        "/oes/policy/applications/{applicationId}/services/{serviceId}/images");
    applicationFeatureRbacEndpoints.add("/oes/policy/gate/{gateId}/status");

    endpointsWithApplicationId.add("/oes/appOnboarding/deleteApplication/{applicationId}");
    endpointsWithApplicationId.add("/oes/appOnboarding/applications/{applicationId}/environments");
    endpointsWithApplicationId.add("/oes/appOnboarding/applications/{applicationId}/imagesource");
    endpointsWithApplicationId.add(
        "/oes/appOnboarding/applications/{applicationId}/services/{serviceId}");
    endpointsWithApplicationId.add("/oes/appOnboarding/applications/{applicationId}");
    endpointsWithApplicationId.add(
        "/oes/policy/applications/{applicationId}/services/{serviceId}/images");

    endpointsWithGateId.add("/oes/policy/gate/{gateId}/executions/{executionId}/status");
    endpointsWithGateId.add("/oes/policy/gate/{gateId}");
    endpointsWithGateId.add("/oes/policy/gate/{gateId}/status");
  }

  private static void populateAnalyticServiceApis() {

    applicationFeatureRbacEndpoints.add("/autopilot/api/v1/applications/latestcanary");
    applicationFeatureRbacEndpoints.add("/autopilot/api/v1/dashboardservice/applications");
    applicationFeatureRbacEndpoints.add("/autopilot/api/v1/applications/{applicationId}");
    applicationFeatureRbacEndpoints.add("/autopilot/api/v1/applications/{id}");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v1/dashboardservice/applications/{applicationId}");
    applicationFeatureRbacEndpoints.add("/autopilot/api/v1/applications");
    applicationFeatureRbacEndpoints.add("/autopilot/api/v1/applications/{id}");
    applicationFeatureRbacEndpoints.add("/autopilot/api/v1/applications/{applicationId}/details");
    applicationFeatureRbacEndpoints.add("/autopilot/canaries/applicationNames");
    applicationFeatureRbacEndpoints.add("/autopilot/canaries/applicationServiceNames");
    applicationFeatureRbacEndpoints.add("/autopilot/canaries/verification/getVerificationSummary");
    applicationFeatureRbacEndpoints.add("/autopilot/canaries/verification/getVerificationHistory");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v1/correlation/log/{riskAnalysisId}/{serviceId}");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v1/correlation/metric/{riskAnalysisId}/{serviceId}");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v1/correlation/log/{riskAnalysisId}/{serviceId}/{clusterId}");
    applicationFeatureRbacEndpoints.add("/autopilot/api/v1/applications/{applicationId}/tags/{id}");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v1/applications/{applicationId}/service/{serviceId}");
    applicationFeatureRbacEndpoints.add("/autopilot/api/v1/applications/{applicationId}/tags");
    applicationFeatureRbacEndpoints.add("/autopilot/api/v1/applications/{applicationId}/tags/{id}");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v1/applications/{applicationId}/logTemplates/{logTemplateName}");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v1/applications/{applicationId}/metricTemplates/{metricTemplateName}");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v1/applications/{applicationId}/deleteMetricTemplate/{templateName}");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v1/applications/{applicationId}/metricTemplates");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v1/applications/{applicationId}/service/{serviceId}/template");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v1/applications/{applicationId}/service/{serviceId}");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v1/applications/{applicationId}/logTemplates");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v1/applications/{applicationId}/logTemplates/{logTemplateName}");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v1/applications/{applicationId}/metricTemplates");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v1/applications/{applicationId}/deleteLogTemplate/{templateName}");
    applicationFeatureRbacEndpoints.add("/autopilot/logs/template/{applicationId}/{templateId}");
    applicationFeatureRbacEndpoints.add("/autopilot/api/v2/applications/getApplicationHealth");
    applicationFeatureRbacEndpoints.add("/autopilot/api/v2/serviceGates/{gateId}");
    applicationFeatureRbacEndpoints.add("/autopilot/api/v2/applications/{id}");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v2/applications/{applicationId}/service/{serviceId}/gates/{id}");
    applicationFeatureRbacEndpoints.add("/autopilot/api/v2/applications/{id}/failures_count");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v2/applications/{applicationId}/service/{serviceId}/gates/{id}/score");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v2/applications/{applicationId}/services/{serviceId}/failures_count");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v2/applications/{applicationId}/service/{serviceId}/template");
    applicationFeatureRbacEndpoints.add("/autopilot/api/v3/applications/{applicationId}/details");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v3/applications/{applicationId}/service/{serviceId}");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v3/applications/{applicationId}/gates/{gateId}");
    applicationFeatureRbacEndpoints.add("/autopilot/api/v3/applications/{applicationId}/service");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v3/applications/{applicationId}/services/{serviceId}/images");
    applicationFeatureRbacEndpoints.add("/autopilot/api/v3/applications/{applicationId}/gates");
    applicationFeatureRbacEndpoints.add(
        "/autopilot/api/v3/applications/{applicationId}/service/{serviceId}/gates/{gateId}/images/score");

    endpointsWithApplicationId.add("/autopilot/api/v1/applications/{applicationId}");
    endpointsWithApplicationId.add("/autopilot/api/v1/applications/{id}");
    endpointsWithApplicationId.add(
        "/autopilot/api/v1/dashboardservice/applications/{applicationId}");
    endpointsWithApplicationId.add("/autopilot/api/v1/applications/{applicationId}/details");
    endpointsWithApplicationId.add("/autopilot/api/v1/applications/{applicationId}/tags/{id}");
    endpointsWithApplicationId.add(
        "/autopilot/api/v1/applications/{applicationId}/service/{serviceId}");
    endpointsWithApplicationId.add("/autopilot/api/v1/applications/{applicationId}/tags");
    endpointsWithApplicationId.add(
        "/autopilot/api/v1/applications/{applicationId}/logTemplates/{logTemplateName}");
    endpointsWithApplicationId.add(
        "/autopilot/api/v1/applications/{applicationId}/metricTemplates/{metricTemplateName}");
    endpointsWithApplicationId.add(
        "/autopilot/api/v1/applications/{applicationId}/deleteMetricTemplate/{templateName}");
    endpointsWithApplicationId.add(
        "/autopilot/api/v1/applications/{applicationId}/metricTemplates");
    endpointsWithApplicationId.add(
        "/autopilot/api/v1/applications/{applicationId}/service/{serviceId}/template");
    endpointsWithApplicationId.add(
        "/autopilot/api/v1/applications/{applicationId}/service/{serviceId}");
    endpointsWithApplicationId.add("/autopilot/api/v1/applications/{applicationId}/logTemplates");
    endpointsWithApplicationId.add(
        "/autopilot/api/v1/applications/{applicationId}/logTemplates/{logTemplateName}");
    endpointsWithApplicationId.add(
        "/autopilot/api/v1/applications/{applicationId}/deleteLogTemplate/{templateName}");
    endpointsWithApplicationId.add("/autopilot/logs/template/{applicationId}/{templateId}");
    endpointsWithApplicationId.add("/autopilot/api/v2/applications/{id}");
    endpointsWithApplicationId.add(
        "/autopilot/api/v2/applications/{applicationId}/service/{serviceId}/gates/{id}");
    endpointsWithApplicationId.add("/autopilot/api/v2/applications/{id}/failures_count");
    endpointsWithApplicationId.add(
        "/autopilot/api/v2/applications/{applicationId}/service/{serviceId}/gates/{id}/score");
    endpointsWithApplicationId.add(
        "/autopilot/api/v2/applications/{applicationId}/services/{serviceId}/failures_count");
    endpointsWithApplicationId.add(
        "/autopilot/api/v2/applications/{applicationId}/service/{serviceId}/template");
    endpointsWithApplicationId.add("/autopilot/api/v3/applications/{applicationId}/details");
    endpointsWithApplicationId.add(
        "/autopilot/api/v3/applications/{applicationId}/service/{serviceId}");
    endpointsWithApplicationId.add("/autopilot/api/v3/applications/{applicationId}/gates/{gateId}");
    endpointsWithApplicationId.add("/autopilot/api/v3/applications/{applicationId}/service");
    endpointsWithApplicationId.add(
        "/autopilot/api/v3/applications/{applicationId}/services/{serviceId}/images");
    endpointsWithApplicationId.add("/autopilot/api/v3/applications/{applicationId}/gates");
    endpointsWithApplicationId.add(
        "/autopilot/api/v3/applications/{applicationId}/service/{serviceId}/gates/{gateId}/images/score");

    endpointsWithGateId.add("/autopilot/api/v2/serviceGates/{gateId}");
  }
}
