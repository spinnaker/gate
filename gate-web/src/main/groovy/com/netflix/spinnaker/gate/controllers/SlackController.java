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

package com.netflix.spinnaker.gate.controllers;

import com.netflix.spinnaker.gate.services.SlackService;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/slack")
@ConfigurationProperties("slack")
@ConditionalOnProperty("slack.token")
public class SlackController {

  private static final Logger log = LoggerFactory.getLogger(SlackController.class);
  private final AtomicReference<List<Map>> slackChannelsCache =
      new AtomicReference<>(new ArrayList<>());

  @Value("${slack.token}")
  String token;

  @Autowired SlackService slackService;

  @ApiOperation("Retrieve a list of public slack channels")
  @RequestMapping("/channels")
  List<Map> getChannels() {
    return slackChannelsCache.get();
  }

  @Scheduled(fixedDelay = 300000L)
  void refreshSlack() {
    try {
      log.info("Refreshing Slack channels list");
      List<Map> channels = fetchChannels();
      log.info("Fetched {} Slack channels", channels.size());
      slackChannelsCache.set(channels);
    } catch (Exception e) {
      log.error("Unable to refresh Slack service list", e);
    }
  }

  List<Map> fetchChannels() {
    SlackService.SlackChannelsResult response = slackService.getChannels(token, null);
    List<Map> channels = response.channels;
    String cursor = response.response_metadata.next_cursor;
    while (cursor != null & cursor.length() > 0) {
      response = slackService.getChannels(token, cursor);
      cursor = response.response_metadata.next_cursor;
      channels.addAll(response.channels);
    }

    return channels;
  }
}
