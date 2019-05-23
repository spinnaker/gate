/*
 * Copyright 2019 OpsMX, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package com.netflix.spinnaker.gate.config;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@ConditionalOnExpression("${security.staticform.enabled:false}")
@Configuration
public class GateWebLoginTemplateConfig implements WebMvcConfigurer {

  private static final Logger log = LoggerFactory.getLogger(GateWebLoginTemplateConfig.class);

  @Value("${security.staticform.resourcepath:/opt/spinnaker/config/}")
  private String staticResourcePath;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {

    if (staticResourcePath != null) {
      registry.addResourceHandler("/**").addResourceLocations("file:" + staticResourcePath);
      log.info("Login form Static Resource path : " + staticResourcePath);
    }
    log.info("StaticResourcePath is registered with Resource Handler");
  }

  public void addViewControllers(ViewControllerRegistry registry) {

    log.debug(
        "Is Login Template file present? : "
            + new File(staticResourcePath + "spinnakerlogin.html").isFile());

    if (new File(staticResourcePath + "spinnakerlogin.html").isFile()) {

      registry.addViewController("/spinnakerlogin").setViewName("redirect:/spinnakerlogin.html");

      log.info(
          "Redirected URL pattern 'spinnakerlogin' view to  Login Template file: spinnakerlogin.html");

    } else {

      registry.addViewController("/spinnakerlogin").setViewName("redirect:/login");

      log.info("Redirected URL pattern 'spinnakerlogin' view to default basic login form ");
    }
  }
}
