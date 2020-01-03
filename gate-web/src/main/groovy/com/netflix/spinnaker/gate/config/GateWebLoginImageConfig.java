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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.netflix.spinnaker.gate.services.LoginImageService;

@ConditionalOnExpression("${security.login.image.enabled:false}")
@Configuration
@EnableConfigurationProperties(GateLoginImageConfigProperties.class)
public class GateWebLoginImageConfig implements WebMvcConfigurer {

  private static final Logger log = LoggerFactory.getLogger(GateWebLoginImageConfig.class);


  @Value("${security.login.image.resourcepath:/opt/spinnaker/config/}")
  private String imageResourcePath; 

  @Value("${security.login.image.url:image.png}")
  private String imageUrl;

  @Autowired(required = false)
  LoginImageService loginImageService;
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {  

    if (imageResourcePath != null) {
      registry.addResourceHandler("/images/**").addResourceLocations("file:" + imageResourcePath)
      .setCachePeriod(0); 
      log.info("Login form image Resource path : " + imageResourcePath);
    }
    if(!imageUrl.equalsIgnoreCase("image.png")){  
    	log.info("Login form image Resource URL : " + imageUrl);    	
    	loginImageService.saveImage(imageUrl,imageResourcePath+"logo.png" );
	}

    log.info("Image ResourcePath is registered with Resource Handler");
  } 

} 
