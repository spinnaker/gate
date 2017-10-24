package com.netflix.spinnaker.gate.controllers

import com.netflix.spinnaker.gate.services.EcsClusterService
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ecsClusters")
class EcsClusterController {
  @Autowired
  EcsClusterService ecsClusterService

  @ApiOperation(value = "Retrieve a list of ECS clusters that can be used for the account and region.")
  @RequestMapping(value = "/{account}/{region}", method = RequestMethod.GET)
  List all(@PathVariable String account, @PathVariable String region) {
    ecsClusterService.getEcsClusters(account, region)
  }
}
