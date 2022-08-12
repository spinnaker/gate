package com.opsmx.spinnaker.gate.security.saml;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

@ConditionalOnExpression("${saml.enabled:false}")
@Configuration
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SamlSsoEventPublishConfig {

  private ApplicationEventPublisher applicationEventPublisher;

  @Autowired
  @Qualifier("springSecurityFilterChain")
  private Filter springSecurityFilterChain;

  @Autowired
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Bean
  public FilterChainProxy getFilters() {
    FilterChainProxy filterChainProxy = (FilterChainProxy) springSecurityFilterChain;
    List<SecurityFilterChain> list = filterChainProxy.getFilterChains();

    list.stream()
        .flatMap(chain -> chain.getFilters().stream())
        .filter(filter -> filter.getClass() == FilterChainProxy.class)
        .findAny()
        .map(FilterChainProxy.class::cast)
        .map(FilterChainProxy::getFilterChains)
        .orElse(new ArrayList<>())
        .stream()
        .flatMap(chin -> chin.getFilters().stream())
        .filter(filter -> filter.getClass() == SAMLProcessingFilter.class)
        .findAny()
        .map(SAMLProcessingFilter.class::cast)
        .ifPresent(filter -> filter.setApplicationEventPublisher(applicationEventPublisher));
    return filterChainProxy;
  }
}
