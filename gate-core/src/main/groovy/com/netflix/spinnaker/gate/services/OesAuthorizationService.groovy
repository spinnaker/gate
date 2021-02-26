
package com.netflix.spinnaker.gate.services

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import java.util.Collection


@FeignClient(name = "OES", url = '${services.platform.baseUrl}')
interface OesAuthorizationService {

  @PutMapping(value = "/platformservice/v2/usergroups/importAndCache", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  ResponseEntity<Object> cacheUserGroups(@RequestBody Collection<String> data, @RequestHeader(value = "x-spinnaker-user") String userName)

}
