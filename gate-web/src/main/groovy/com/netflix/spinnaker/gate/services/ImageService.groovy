/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.netflix.spinnaker.gate.services

import com.netflix.spinnaker.gate.services.internal.ClouddriverServiceSelector
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@CompileStatic
@Component
class ImageService {

  @Autowired
  ClouddriverServiceSelector clouddriverServiceSelector

  @Autowired
  ProviderLookupService providerLookupService

  List<Map> getForAccountAndRegion(String provider, String account, String region, String imageId, String selectorKey) {
    clouddriverServiceSelector.select().getImageDetails(provider, account, region, imageId)
  }

  List<Map> search(String provider, String query, String region, String account, Integer count, Map<String, Object> additionalFilters, String selectorKey) {
    clouddriverServiceSelector.select().findImages(provider, query, region, account, count, additionalFilters)
  }

  List<String> findTags(String provider, String account, String repository, String selectorKey) {
    clouddriverServiceSelector.select().findTags(provider, account, repository)
  }
}
