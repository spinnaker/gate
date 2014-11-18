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

package com.netflix.spinnaker.gate.controllers

import groovy.transform.CompileStatic
import org.springframework.web.context.request.async.DeferredResult

@CompileStatic
class AsyncControllerSupport {
  static <T> DeferredResult<T> deferOne(rx.Observable<T> obs) {
    def q = new DeferredResult<T>()
    obs.limit(1).subscribe({ q.result = it}, { q.errorResult = it})
    q
  }

  static <T> DeferredResult<List<T>> deferAll(rx.Observable<T> obs) {
    def q = new DeferredResult<List<T>>()
    List<T> result = []
    obs.subscribe({ //onNext
      result.add(it)
    },{ //onError
      q.errorResult = it
    }, { //onComplete
      q.result = result
    })
    q
  }


  static <T> DeferredResult<T> defer(rx.Observable<T> obs) {
    deferOne(obs)
  }
}
