package com.netflix.spinnaker.gate.config;

import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public interface RequestMatcherProvider {

  class AnyProvider implements RequestMatcherProvider {}

  default RequestMatcher requestMatcher() {
    return AnyRequestMatcher.INSTANCE;
  }
}
