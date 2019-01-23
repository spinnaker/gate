package com.netflix.spinnaker.gate.controllers

import com.netflix.spinnaker.gate.services.GremlinService
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping('/gremlin')
class GremlinController {
  final static String APIKEY_KEY = "apiKey";

  @Autowired
  GremlinService gremlinService

  @ApiOperation(value = 'Retrieve a list of gremlin command templates')
  @RequestMapping(value = '/templates/command', method = RequestMethod.POST)
  List listCommandTemplates(@RequestBody(required = true) Map apiKeyMap) {
    final String apiKeyValue = apiKeyMap.get(APIKEY_KEY);
    gremlinService.getCommandTemplates("Key " + apiKeyValue)
  }

  @ApiOperation(value = 'Retrieve a list of gremlin target templates')
  @RequestMapping(value = '/templates/target', method = RequestMethod.POST)
  List listTargetTemplates(@RequestBody(required = true) Map apiKeyMap) {
    final String apiKeyValue = apiKeyMap.get(APIKEY_KEY);
    gremlinService.getTargetTemplates("Key " + apiKeyValue)
  }
}
