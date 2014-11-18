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

package com.netflix.spinnaker.gate.services

import com.google.common.base.Preconditions
import com.netflix.hystrix.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import rx.Observable
import rx.functions.Func1
import rx.schedulers.Schedulers

@Component
class ApplicationService {
  private static final String SERVICE = "applications"
  private static final HystrixCommandGroupKey HYSTRIX_KEY = HystrixCommandGroupKey.Factory.asKey(SERVICE)

  @Autowired
  OortService oortService

  @Autowired
  MayoService mayoService

  @Autowired
  OrcaService orcaService

  @Autowired
  Front50Service front50Service

  @Autowired
  CredentialsService credentialsService

  static HystrixCommandProperties.Setter createHystrixCommandPropertiesSetter() {
    HystrixCommandProperties.invokeMethod("Setter", null)
  }

  Observable<Map> getAll() {
    new HystrixObservableCommand<Map>(HystrixObservableCommand.Setter.withGroupKey(HYSTRIX_KEY)
        .andCommandKey(HystrixCommandKey.Factory.asKey("getAll"))
        .andCommandPropertiesDefaults(createHystrixCommandPropertiesSetter()
        .withExecutionIsolationThreadTimeoutInMilliseconds(30000))) {
      @Override
      protected Observable<Map> run() {
        credentialsService.getAccountNames().flatMap ({ front50Service.getAll(it) } as Func1<String, Observable<Map>>)
      }

      @Override
      protected Observable<Map> getFallback() {
        Observable.empty()
      }

      @Override
      protected String getCacheKey() {
        "applications-all"
      }
    }.observe()
  }

  Observable<Map> get(String name) {
    new HystrixObservableCommand<Map>(HystrixObservableCommand.Setter.withGroupKey(HYSTRIX_KEY)
        .andCommandKey(HystrixCommandKey.Factory.asKey("getApp-${name}"))
        .andCommandPropertiesDefaults(createHystrixCommandPropertiesSetter()
        .withExecutionIsolationThreadTimeoutInMilliseconds(30000))) {
      @Override
      protected Observable<Map> run() {
        Observable.mergeDelayError(
                oortService.getApplication(name),
                credentialsService.accountNames.flatMap({ String account -> front50Service.getMetaData(account, name) } as Func1<String, Observable<Map>>))
                .onErrorReturn { t -> [:] }
                .observeOn(Schedulers.io())
                .reduce([attributes: [:]], { Map app, Map data ->
          if (data.containsKey('clusters')) {
            app.putAll(data)
          } else {
            app.attributes.putAll(data)
          }
          app
        })
      }

      @Override
      protected Observable<Map> getFallback() {
        Observable.empty()
      }

      @Override
      protected String getCacheKey() {
        "applications-${name}"
      }
    }.observe()

  }

  // TODO Hystrix fallback?
  Observable<List> getTasks(String app) {
    Preconditions.checkNotNull(app)
    orcaService.getTasks(app)
  }
  // TODO Hystrix fallback?
  Observable<List> getPipelines(String app) {
    Preconditions.checkNotNull(app)
    orcaService.getPipelines(app)
  }

  Observable<List<Map>> getPipelineConfigs(String app) {
    new PipelineConfigsCommand(app, mayoService).observe()
  }

  Observable<Map> getPipelineConfig(String app, String pipelineName) {
    new PipelineConfigCommand(app, pipelineName, mayoService).observe()
  }

  Observable<Map> bake(String application, String pkg, String baseOs, String baseLabel, String region) {
    orcaService.doOperation([application: application, description: "Bake (Gate)",
                             job        : [[type  : "bake", "package": pkg, baseOs: baseOs, baseLabel: baseLabel,
                                            region: region, user: "gate"]]])
  }

  Observable<Map> delete(String account, String name) {
    front50Service.delete(account, name)
  }

  Observable<Map> create(Map<String, String> app) {
    def account = app.remove("account")
    orcaService.doOperation([application: app.name, description: "Create application (Gate)",
                             job        : [[type: "createApplication", application: app, account: account]]])
  }

  static class PipelineConfigsCommand extends HystrixObservableCommand<List<Map>> {
    final String app
    final MayoService mayoService

    static List<Map> last = []

    PipelineConfigsCommand(String app, MayoService mayoService) {
      super(HystrixObservableCommand.Setter.withGroupKey(HYSTRIX_KEY)
          .andCommandKey(HystrixCommandKey.Factory.asKey("getPipelineConfigs-${app}"))
          .andCommandPropertiesDefaults(ApplicationService.createHystrixCommandPropertiesSetter()
          .withExecutionIsolationThreadTimeoutInMilliseconds(30000)))
      this.app = app
      this.mayoService = mayoService
    }

    @Override
    protected Observable<List<Map>> run() {
      mayoService.getPipelineConfigs(app).map {
        last = it
      }
    }

    @Override
    protected Observable<List<Map>> getFallback() {
      Observable.just(last)
    }
  }

  static class PipelineConfigCommand extends HystrixObservableCommand<Map> {
    final String app
    final String pipelineName
    final MayoService mayoService

    static Map last = [:]

    PipelineConfigCommand(String app, String pipelineName, MayoService mayoService) {
      super(HystrixObservableCommand.Setter.withGroupKey(HYSTRIX_KEY)
          .andCommandKey(HystrixCommandKey.Factory.asKey("getPipelineConfig-${app}-${pipelineName}"))
          .andCommandPropertiesDefaults(ApplicationService.createHystrixCommandPropertiesSetter()
          .withExecutionIsolationThreadTimeoutInMilliseconds(30000)))
      this.app = app
      this.pipelineName = pipelineName
      this.mayoService = mayoService
    }

    @Override
    protected Observable<Map> run() {
      mayoService.getPipelineConfig(app, pipelineName).map {
        last = it
      }
    }

    @Override
    protected Observable<Map> getFallback() {
      Observable.just(last)
    }
  }
}
