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

package com.netflix.spinnaker.gate.config

import org.springframework.session.data.redis.config.ConfigureRedisAction

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext
import com.netflix.spectator.api.Registry
import com.netflix.spinnaker.filters.AuthenticatedRequestFilter
import com.netflix.spinnaker.gate.retrofit.EurekaOkClient
import com.netflix.spinnaker.gate.retrofit.Slf4jRetrofitLogger
import com.netflix.spinnaker.gate.services.EurekaLookupService
import com.netflix.spinnaker.gate.services.internal.*
import com.squareup.okhttp.OkHttpClient
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import retrofit.Endpoint
import retrofit.RequestInterceptor
import retrofit.RestAdapter
import retrofit.converter.JacksonConverter

import javax.servlet.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static retrofit.Endpoints.newFixedEndpoint

@CompileStatic
@Configuration
@Slf4j
class GateConfig extends RedisHttpSessionConfiguration {

  @Value('${server.session.timeoutInSeconds:3600}')
  void setSessionTimeout(int maxInactiveIntervalInSeconds) {
    super.setMaxInactiveIntervalInSeconds(maxInactiveIntervalInSeconds)
  }

  @Value('${retrofit.logLevel:BASIC}')
  String retrofitLogLevel

  @Autowired
  RequestInterceptor spinnakerRequestInterceptor

  @Bean
  JedisConnectionFactory jedisConnectionFactory(
    @Value('${redis.connection:redis://localhost:6379}') String connection
  ) {
    URI redis = URI.create(connection)
    def factory = new JedisConnectionFactory()
    factory.hostName = redis.host
    factory.port = redis.port
    if (redis.userInfo) {
      factory.password = redis.userInfo.split(":", 2)[1]
    }
    factory
  }

  @Bean
  @ConditionalOnMissingBean(RestTemplate)
  RestTemplate restTemplate() {
    new RestTemplate()
  }

  @Bean
  @ConditionalOnProperty("redis.configuration.secure")
  ConfigureRedisAction configureRedisAction() {
    return ConfigureRedisAction.NO_OP
  }

  @Bean
  ExecutorService executorService() {
    Executors.newCachedThreadPool()
  }

  @Autowired
  Registry registry

  @Autowired
  EurekaLookupService eurekaLookupService

  @Autowired
  ServiceConfiguration serviceConfiguration

  @Bean
  OrcaService orcaService(OkHttpClient okHttpClient) {
    createClient "orca", OrcaService, okHttpClient
  }

  @Bean
  @ConditionalOnProperty("services.fiat.enabled")
  FiatService fiatService(OkHttpClient okHttpClient) {
    createClient "fiat", FiatService, okHttpClient
  }

  @Bean
  Front50Service front50Service(OkHttpClient okHttpClient) {
    createClient "front50", Front50Service, okHttpClient
  }

  @Bean
  ClouddriverService clouddriverService(OkHttpClient okHttpClient) {
    createClient "clouddriver", ClouddriverService, okHttpClient
  }

  //---- semi-optional components:
  @Bean
  @ConditionalOnProperty('services.rosco.enabled')
  RoscoService roscoService(OkHttpClient okHttpClient) {
    createClient "rosco", RoscoService, okHttpClient
  }

  //---- optional backend components:
  @Bean
  @ConditionalOnProperty('services.echo.enabled')
  EchoService echoService(OkHttpClient okHttpClient) {
    createClient "echo", EchoService, okHttpClient
  }

  @Bean
  @ConditionalOnProperty('services.igor.enabled')
  IgorService igorService(OkHttpClient okHttpClient) {
    createClient "igor", IgorService, okHttpClient
  }

  @Bean
  @ConditionalOnProperty('services.mine.enabled')
  MineService mineService(OkHttpClient okHttpClient) {
    createClient "mine", MineService, okHttpClient
  }

  private <T> T createClient(String serviceName, Class<T> type, OkHttpClient okHttpClient) {
    Service service = serviceConfiguration.getService(serviceName)
    if (service == null) {
      throw new IllegalArgumentException("Unknown service ${serviceName} requested of type ${type}")
    }
    if (!service.enabled) {
      return null
    }
    Endpoint endpoint = serviceConfiguration.discoveryHosts && service.vipAddress ?
      newFixedEndpoint("niws://${service.vipAddress}")
      : newFixedEndpoint(service.baseUrl)

    def client = new EurekaOkClient(okHttpClient, registry, serviceName, eurekaLookupService)

    new RestAdapter.Builder()
      .setRequestInterceptor(spinnakerRequestInterceptor)
      .setEndpoint(endpoint)
      .setClient(client)
      .setConverter(new JacksonConverter())
      .setLogLevel(RestAdapter.LogLevel.valueOf(retrofitLogLevel))
      .setLog(new Slf4jRetrofitLogger(type))
      .build()
      .create(type)
  }

  @Bean
  FilterRegistrationBean simpleCORSFilter() {
    def frb = new FilterRegistrationBean(
      new Filter() {
        public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
          throws IOException, ServletException {
          HttpServletResponse response = (HttpServletResponse) res;
          HttpServletRequest request = (HttpServletRequest) req;
          String origin = request.getHeader("Origin") ?: "*"
          response.setHeader("Access-Control-Allow-Credentials", "true");
          response.setHeader("Access-Control-Allow-Origin", origin);
          response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, PATCH");
          response.setHeader("Access-Control-Max-Age", "3600");
          response.setHeader("Access-Control-Allow-Headers", "x-requested-with, content-type, authorization");
          response.setHeader("Access-Control-Expose-Headers", [Headers.AUTHENTICATION_REDIRECT_HEADER_NAME].join(", "))
          chain.doFilter(req, res);
        }

        public void init(FilterConfig filterConfig) {}

        public void destroy() {}
      })
    frb.setOrder(Ordered.HIGHEST_PRECEDENCE)

    return frb
  }

  /**
   * This AuthenticatedRequestFilter pulls the email and accounts out of the Spring
   * security context in order to enabling forwarding them to downstream components.
   */
  @Bean
  FilterRegistrationBean authenticatedRequestFilter() {
    def frb = new FilterRegistrationBean(new AuthenticatedRequestFilter(false))
    frb.order = Ordered.LOWEST_PRECEDENCE
    return frb
  }

  /**
   * This pulls the `springSecurityFilterChain` in front of the {@link AuthenticatedRequestFilter},
   * because the user must be authenticated through the security filter chain before his username/credentials
   * can be pulled and forwarded in the AuthenticatedRequestFilter.
   */
  @Bean
  public FilterRegistrationBean securityFilterChain(@Qualifier(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME) Filter securityFilter) {
    def frb = new FilterRegistrationBean(securityFilter)
    frb.order = 0
    frb.name = AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME
    return frb;
  }

  @Component
  static class HystrixFilter implements Filter {
    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
      HystrixRequestContext.initializeContext()
      chain.doFilter(request, response)
    }

    void init(FilterConfig filterConfig) throws ServletException {}

    void destroy() {}
  }
}
