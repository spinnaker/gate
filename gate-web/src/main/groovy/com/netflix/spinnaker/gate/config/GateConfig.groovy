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

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext
import com.netflix.spectator.api.Registry
import com.netflix.spinnaker.fiat.shared.FiatClientConfigurationProperties
import com.netflix.spinnaker.fiat.shared.FiatPermissionEvaluator
import com.netflix.spinnaker.fiat.shared.FiatService
import com.netflix.spinnaker.fiat.shared.FiatStatus
import com.netflix.spinnaker.filters.AuthenticatedRequestFilter
import com.netflix.spinnaker.gate.filters.CorsFilter
import com.netflix.spinnaker.gate.filters.GateOriginValidator
import com.netflix.spinnaker.gate.filters.OriginValidator
import com.netflix.spinnaker.gate.retrofit.EurekaOkClient
import com.netflix.spinnaker.gate.retrofit.Slf4jRetrofitLogger
import com.netflix.spinnaker.gate.services.EurekaLookupService
import com.netflix.spinnaker.gate.services.internal.ClouddriverService
import com.netflix.spinnaker.gate.services.internal.ClouddriverServiceSelector
import com.netflix.spinnaker.gate.services.internal.EchoService
import com.netflix.spinnaker.gate.services.internal.Front50Service
import com.netflix.spinnaker.gate.services.internal.IgorService
import com.netflix.spinnaker.gate.services.internal.KayentaService
import com.netflix.spinnaker.gate.services.internal.MineService
import com.netflix.spinnaker.gate.services.internal.OrcaService
import com.netflix.spinnaker.gate.services.internal.OrcaServiceSelector
import com.netflix.spinnaker.gate.services.internal.RoscoService
import com.netflix.spinnaker.kork.dynamicconfig.DynamicConfigService
import com.netflix.spinnaker.kork.web.selector.DefaultServiceSelector
import com.netflix.spinnaker.kork.web.selector.SelectableService
import com.netflix.spinnaker.kork.web.selector.ServiceSelector
import com.squareup.okhttp.OkHttpClient
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer
import org.springframework.session.data.redis.config.ConfigureRedisAction
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils
import org.springframework.web.client.RestTemplate
import redis.clients.jedis.JedisPool
import retrofit.Endpoint
import retrofit.RequestInterceptor
import retrofit.RestAdapter
import retrofit.converter.JacksonConverter

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static retrofit.Endpoints.newFixedEndpoint

@CompileStatic
@Configuration
@Slf4j
@EnableConfigurationProperties(FiatClientConfigurationProperties)
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
  JedisPool jedis(@Value('${redis.connection:redis://localhost:6379}') String connection,
                  @Value('${redis.timeout:2000}') int timeout) {
    return new JedisPool(new URI(connection), timeout)
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
  OrcaServiceSelector orcaServiceSelector(OkHttpClient okHttpClient) {
    return new OrcaServiceSelector(createClientSelector("orca", OrcaService, okHttpClient))
  }

  @Bean
  FiatService fiatService(OkHttpClient okHttpClient) {
    // always create the fiat service even if 'services.fiat.enabled' is 'false' (it can be enabled dynamically)
    createClient "fiat", FiatService, okHttpClient, null, true
  }

  @Bean
  Front50Service front50Service(OkHttpClient okHttpClient) {
    createClient "front50", Front50Service, okHttpClient
  }

  @Bean
  ClouddriverService clouddriverService(OkHttpClient okHttpClient) {
    createClient "clouddriver", ClouddriverService, okHttpClient
  }

  @Bean
  ClouddriverServiceSelector clouddriverServiceSelector(ClouddriverService defaultClouddriverService, OkHttpClient okHttpClient) {
    // support named clouddriver service clients
    Map<String, ClouddriverService> dynamicServices = [:]
    if (serviceConfiguration.getService("clouddriver").getConfig().containsKey("dynamicEndpoints")) {
      def endpoints = (Map<String, String>) serviceConfiguration.getService("clouddriver").getConfig().get("dynamicEndpoints")
      dynamicServices = (Map<String, ClouddriverService>) endpoints.collectEntries { k, v ->
        [k, createClient("clouddriver", ClouddriverService, okHttpClient, k)]
      }
    }

    return new ClouddriverServiceSelector(defaultClouddriverService, dynamicServices)
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

  @Bean
  @ConditionalOnProperty('services.kayenta.enabled')
  KayentaService kayentaService(OkHttpClient okHttpClient) {
    createClient "kayenta", KayentaService, okHttpClient
  }

  private <T> T createClient(String serviceName,
                             Class<T> type,
                             OkHttpClient okHttpClient,
                             String dynamicName = null,
                             boolean forceEnabled = false) {
    Service service = serviceConfiguration.getService(serviceName)
    if (service == null) {
      throw new IllegalArgumentException("Unknown service ${serviceName} requested of type ${type}")
    }

    if (!service.enabled && !forceEnabled) {
      return null
    }

    Endpoint endpoint
    if (dynamicName == null) {
      endpoint = serviceConfiguration.discoveryHosts && service.vipAddress ?
        newFixedEndpoint("niws://${service.vipAddress}")
        : newFixedEndpoint(service.baseUrl)
    } else {
      if (!service.getConfig().containsKey("dynamicEndpoints")) {
        throw new IllegalArgumentException("Unknown dynamicEndpoint ${dynamicName} for service ${serviceName} of type ${type}")
      }
      endpoint = newFixedEndpoint(((Map<String, String>) service.getConfig().get("dynamicEndpoints")).get(dynamicName))
    }

    def client = new EurekaOkClient(okHttpClient, registry, serviceName, eurekaLookupService)
    buildService(client, type, endpoint)
  }

  private <T> T buildService(EurekaOkClient client, Class<T> type, Endpoint endpoint) {
    // New role providers break deserialization if this is not enabled.
    ObjectMapper objectMapper = new ObjectMapper()
      .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    new RestAdapter.Builder()
      .setRequestInterceptor(spinnakerRequestInterceptor)
      .setEndpoint(endpoint)
      .setClient(client)
      .setConverter(new JacksonConverter(objectMapper))
      .setLogLevel(RestAdapter.LogLevel.valueOf(retrofitLogLevel))
      .setLog(new Slf4jRetrofitLogger(type))
      .build()
      .create(type)
  }

  private <T> SelectableService createClientSelector(String serviceName, Class<T> type, OkHttpClient okHttpClient) {
    Service service = serviceConfiguration.getService(serviceName)
    if (CollectionUtils.isEmpty(service?.getBaseUrls())) {
      throw new IllegalArgumentException("Unknown service ${serviceName} requested of type ${type}")
    }

    return new SelectableService(
      service.getBaseUrls().collect {
        def selector = new DefaultServiceSelector(
          buildService(
            new EurekaOkClient(okHttpClient, registry, serviceName, eurekaLookupService),
            type,
            newFixedEndpoint(it.baseUrl)),
          it.priority,
          it.config)

        def selectorClass = it.config?.selectorClass as Class<ServiceSelector>
        if (selectorClass) {
          selector = selectorClass.getConstructors()[0].newInstance(
            selector.service, it.priority, it.config
          )
        }
        selector
      } as List<ServiceSelector>
    )
  }

  @Bean
  OriginValidator gateOriginValidator(
    @Value('${services.deck.baseUrl}') String deckBaseUrl,
    @Value('${services.deck.redirectHostPattern:#{null}}') String redirectHostPattern,
    @Value('${cors.allowedOriginsPattern:#{null}}') String allowedOriginsPattern) {
    return new GateOriginValidator(deckBaseUrl, redirectHostPattern, allowedOriginsPattern)
  }

  @Bean
  FilterRegistrationBean simpleCORSFilter(OriginValidator gateOriginValidator) {
    def frb = new FilterRegistrationBean(new CorsFilter(gateOriginValidator))
    frb.setOrder(Ordered.HIGHEST_PRECEDENCE)
    return frb
  }

  /**
   * This AuthenticatedRequestFilter pulls the email and accounts out of the Spring
   * security context in order to enabling forwarding them to downstream components.
   *
   * Additionally forwards request origin metadata (deck vs api).
   */
  @Bean
  FilterRegistrationBean authenticatedRequestFilter() {
    def frb = new FilterRegistrationBean(new AuthenticatedRequestFilter(false, true, true))
    frb.order = Ordered.LOWEST_PRECEDENCE
    return frb
  }

  /**
   * This pulls the `springSecurityFilterChain` in front of the {@link AuthenticatedRequestFilter},
   * because the user must be authenticated through the security filter chain before his username/credentials
   * can be pulled and forwarded in the AuthenticatedRequestFilter.
   */
  @Bean
  FilterRegistrationBean securityFilterChain(
    @Qualifier(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME) Filter securityFilter) {
    def frb = new FilterRegistrationBean(securityFilter)
    frb.order = 0
    frb.name = AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME
    return frb;
  }

  @Bean
  FiatStatus fiatStatus(DynamicConfigService dynamicConfigService,
                        Registry registry,
                        FiatClientConfigurationProperties fiatClientConfigurationProperties) {
    return new FiatStatus(registry, dynamicConfigService, fiatClientConfigurationProperties)
  }

  @Bean
  FiatPermissionEvaluator fiatPermissionEvaluator(FiatStatus fiatStatus,
                                                  Registry registry,
                                                  FiatService fiatService,
                                                  FiatClientConfigurationProperties fiatClientConfigurationProperties) {
    return new FiatPermissionEvaluator(registry, fiatService, fiatClientConfigurationProperties, fiatStatus)
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
