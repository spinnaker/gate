package com.netflix.spinnaker.gate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class MultiAuthSupport {

  @Value("${default.apiPort:0}")
  private int apiPort;

  @Bean
  RequestMatcherProvider multiAuthRequestMatcherProvider() {
    return new RequestMatcherProvider() {
      @Override
      public RequestMatcher requestMatcher() {
        return req -> req.getLocalPort() != apiPort;
      }
    };
  }
}
