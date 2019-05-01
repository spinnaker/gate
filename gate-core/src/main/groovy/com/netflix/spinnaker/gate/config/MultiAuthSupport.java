package com.netflix.spinnaker.gate.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class MultiAuthSupport {

  @Bean
  @ConditionalOnBean(TomcatWebServer.class)
  RequestMatcherProvider multiAuthRequestMatcherProvider(TomcatWebServer tomcatWebServer) {
    return new RequestMatcherProvider() {
      @Override
      public RequestMatcher requestMatcher() {
        if (tomcatWebServer.getTomcat().getService().findConnectors().length > 1) {
          return req -> req.getLocalPort() == tomcatWebServer.getPort();
        }

        return AnyRequestMatcher.INSTANCE;
      }
    };
  }

  @Bean
  @ConditionalOnMissingBean(RequestMatcherProvider.class)
  RequestMatcherProvider defaultRequestMatcherProvider() {
    return new RequestMatcherProvider.AnyProvider();
  }
}
