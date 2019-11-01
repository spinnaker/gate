/*
 * Copyright 2019 Netflix, Inc.
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

package com.netflix.spinnaker.gate.controllers

import com.netflix.spinnaker.gate.services.SlackService
import com.netflix.spinnaker.security.AuthenticatedRequest
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicReference

@CompileStatic
@RequestMapping("/slack")
@RestController
@ConditionalOnProperty('slack.token')
@Slf4j
class SlackController {
  AtomicReference<List<Map>> slackChannelsCache = new AtomicReference<>([])

  @Autowired
  SlackService slackService

  @RequestMapping("/channels")
  List<Map> getChannels() {
    return slackChannelsCache.get()
  }


  @Scheduled(fixedDelay = 300000L)
  void refreshSlack() {
    try {
      log.info("Refreshing Slack channels list")
      List<Map> channels = fetchChannels()
      log.info("Fetched {} Slack channels", channels?.size())
      slackChannelsCache.set(channels)
    } catch (e) {
      log.error("Unable to refresh Slack service list", e)
    }
  }

  List<Map> fetchChannels() {
    SlackService.SlackChannelsResult response = AuthenticatedRequest.allowAnonymous { slackService.getChannels() }
    List<Map> channels = response?.channels
    return channels
  }
}
