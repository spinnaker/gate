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
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicationFeatureRbac {

  @Autowired private OesAuthorizationService oesAuthorizationService;

  private static final List<String> runtime_access = new ArrayList<>();
  public static final List<String> applicationFeatureRbacEndpoints = new ArrayList<>();
  public static final List<String> endpointsWithApplicationId = new ArrayList<>();
  public static final List<String> endpointsWithServiceId = new ArrayList<>();
  public static final List<String> endpointsWithPipelineId = new ArrayList<>();
  public static final List<String> endpointsWithGateId = new ArrayList<>();

  private static final String YOU_DO_NOT_HAVE = "You do not have : ";
  private static final String PERMISSION_FOR_THE_FEATURE_TYPE =
      " permission for the feature type : ";
  private static final String TO_PERFORM_THIS_OPERATION = " to perform this operation";

  static {
    populateDashboardServiceApis();
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
          "You do not have permission for the feature type : " + RbacFeatureType.APP.name());
    }
  }

  public void authorizeUserForApplicationId(
      String username, String endpointUrl, String httpMethod) {

    HttpMethod method = HttpMethod.valueOf(httpMethod);
    Integer applicationId = getApplicationId(endpointUrl);
    PermissionModel permission;

    log.info("authorizing the endpoint : {}", endpointUrl);

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
                        username, PermissionEnum.view.name(), serviceId, null, null, username)
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
                        username, PermissionEnum.delete.name(), serviceId, null, null, username)
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
                        username, PermissionEnum.view.name(), null, pipelineId, null, username)
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
                        username, PermissionEnum.delete.name(), null, pipelineId, null, username)
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
    applicationFeatureRbacEndpoints.add("/dashboardservice/v2/application/{applicationId}");
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
    applicationFeatureRbacEndpoints.add("/dashboardservice/v2/applications/{applicationId}");
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
}
