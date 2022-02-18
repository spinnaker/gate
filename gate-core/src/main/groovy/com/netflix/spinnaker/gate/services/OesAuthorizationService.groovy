
package com.netflix.spinnaker.gate.services

import com.netflix.spinnaker.gate.model.PermissionModel
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

import java.util.Collection


@FeignClient(name = "OES", url = '${services.platform.baseUrl}')
interface OesAuthorizationService {

  @PutMapping(value = "/platformservice/v2/usergroups/importAndCache", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Object> cacheUserGroups(@RequestBody Collection<String> data, @RequestHeader(value = "x-spinnaker-user") String userName)

  @GetMapping(value = "/platformservice/v6/users/{username}/features/{featureType}", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Map<String, String>> isFeatureVisibility(@PathVariable("username") String username, @PathVariable("featureType") String featureType, @RequestHeader(value = "x-spinnaker-user") String userName)

  @GetMapping(value = "/platformservice/v6/users/{username}/features/{featureType}/{resourceId}/permission", produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<PermissionModel> fetchPermissions(@PathVariable("username") String username, @PathVariable("featureType") String featureType, @PathVariable("resourceId") Integer resourceId, @RequestHeader(value = "x-spinnaker-user") String userName)

  @GetMapping(value = "/platformservice/v6/users/{username}/feature",produces = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Map<String, String>> isAuthorizedUser(@PathVariable("username") String username, @RequestParam("permission") String permission, @RequestParam("serviceId") Integer serviceId,
                                                       @RequestParam("pipelineId") Integer pipelineId, @RequestParam("gateId") Integer gateId, @RequestParam("approvalGateId") Integer approvalGateId,
                                                       @RequestParam("approvalGateInstanceId") Integer approvalGateInstanceId, @RequestParam("approvalGatePolicyId") Integer approvalGatePolicyId,
                                                       @RequestHeader(value = "x-spinnaker-user") String userName)

}
