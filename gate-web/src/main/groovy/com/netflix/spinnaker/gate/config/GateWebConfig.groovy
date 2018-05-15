/*
 * Copyright 2015 Netflix, Inc.
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

import com.netflix.spectator.api.Registry
import com.netflix.spinnaker.gate.interceptors.RequestContextInterceptor
import com.netflix.spinnaker.gate.interceptors.RequestIdInterceptor
import com.netflix.spinnaker.gate.interceptors.RequestLoggingInterceptor
import com.netflix.spinnaker.gate.ratelimit.RateLimitPrincipalProvider
import com.netflix.spinnaker.gate.ratelimit.RateLimiter
import com.netflix.spinnaker.gate.ratelimit.RateLimitingInterceptor
import com.netflix.spinnaker.gate.retrofit.UpstreamBadRequest
import com.netflix.spinnaker.kork.web.interceptors.MetricsInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.filter.ShallowEtagHeaderFilter
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.servlet.handler.HandlerMappingIntrospector
import retrofit.RetrofitError

import javax.servlet.Filter
import javax.servlet.http.HttpServletResponse

@Configuration
@ComponentScan
public class GateWebConfig extends WebMvcConfigurerAdapter {
  @Autowired
  Registry registry

  @Autowired(required = false)
  RateLimitPrincipalProvider rateLimiterPrincipalProvider

  @Autowired(required = false)
  RateLimiter rateLimiter

  @Autowired
  Registry spectatorRegistry

  @Value('${rateLimit.learning:true}')
  Boolean rateLimitLearningMode

  @Value('${requestLogging.enabled:false}')
  Boolean requestLogging

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(
      new MetricsInterceptor(
        this.registry, "controller.invocations", ["account", "region"], ["BasicErrorController"]
      )
    )

    registry.addInterceptor(new RequestIdInterceptor())

    if (requestLogging) {
      registry.addInterceptor(new RequestLoggingInterceptor())
    }

    if (rateLimiter != null) {
      registry.addInterceptor(new RateLimitingInterceptor(rateLimiter, spectatorRegistry, rateLimiterPrincipalProvider))
    }

    registry.addInterceptor(new RequestContextInterceptor())
  }

  @Bean
  HandlerMappingIntrospector mvcHandlerMappingIntrospector(ApplicationContext context) {
    return new HandlerMappingIntrospector(context)
  }

  @Bean
  Filter eTagFilter() {
    new ShallowEtagHeaderFilter()
  }

  @Bean
  UpstreamBadRequestExceptionHandler upstreamBadRequestExceptionHandler() {
    return new UpstreamBadRequestExceptionHandler()
  }

  @ControllerAdvice
  static class UpstreamBadRequestExceptionHandler {
    @ResponseBody
    @ExceptionHandler(UpstreamBadRequest)
    public Map handleUpstreamBadRequest(HttpServletResponse response,
                                        UpstreamBadRequest exception) {
      response.setStatus(exception.status)

      def message = exception.message
      def failureCause = exception.cause
      if (failureCause instanceof RetrofitError) {
        try {
          def retrofitBody = failureCause.getBodyAs(Map) as Map
          message = retrofitBody.error ?: message
        } catch (Exception ignored) {
          // unable to extract error from response
        }

        failureCause = failureCause.cause ?: failureCause
      }

      return [
        failureCause: failureCause.toString(),
        error: HttpStatus.valueOf(exception.status).reasonPhrase,
        message: message,
        status: exception.status,
        url: exception.url,
        timestamp: System.currentTimeMillis()
      ]
    }
  }

  @Override
  void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.favorPathExtension(false)
  }

}
