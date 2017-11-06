package com.netflix.spinnaker.gate.services;

import com.netflix.spinnaker.gate.services.commands.HystrixFactory;
import com.netflix.spinnaker.gate.services.internal.ClouddriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class RoleService {

  ClouddriverService clouddriver;

  @Autowired
  RoleService(ClouddriverService clouddriver) {
    this.clouddriver = clouddriver;
  }

  List getRoles(String provider) {
    HystrixFactory.newListCommand("pipelines", "updatePipeline") {
      clouddriver.getRoles(provider)
    } execute()
  }
}
