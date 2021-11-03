/*
 * Copyright 2021 OpsMx, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package com.opsmx.spinnaker.gate.exception

import com.netflix.spinnaker.gate.controllers.OpsmxAutopilotController
import com.netflix.spinnaker.gate.controllers.OpsmxDashboardController
import com.netflix.spinnaker.gate.controllers.OpsmxOesController
import com.netflix.spinnaker.gate.controllers.OpsmxPlatformController
import com.netflix.spinnaker.gate.controllers.OpsmxVisibilityController
import com.opsmx.spinnaker.gate.controllers.OpsmxAuditClientServiceController
import com.opsmx.spinnaker.gate.controllers.OpsmxAuditServiceController
import com.opsmx.spinnaker.gate.controllers.OpsmxSaporPolicyController
import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import retrofit.RetrofitError

@Slf4j
@ControllerAdvice(basePackageClasses = [OpsmxSaporPolicyController.class, OpsmxAutopilotController.class,
OpsmxAuditClientServiceController.class, OpsmxDashboardController.class, OpsmxPlatformController.class,
OpsmxOesController.class, OpsmxVisibilityController.class, OpsmxAuditServiceController.class])
class RetrofitErrorHandler {

  @ExceptionHandler([RetrofitError.class])
  @ResponseBody ResponseEntity<Object> handleRetrofitError(RetrofitError retrofitError){
    if (retrofitError!=null){
      log.warn("Exception occurred in OES downstream services : {}", retrofitError.getMessage())
      if (retrofitError.getKind() == RetrofitError.Kind.NETWORK){
        NetworkErrorResponseModel networkErrorResponseModel = new NetworkErrorResponseModel()
        networkErrorResponseModel.setErrorType("Network Error")
        networkErrorResponseModel.setErrorMsg(retrofitError.getMessage())
        networkErrorResponseModel.setTimeStampMillis(System.currentTimeMillis())
        networkErrorResponseModel.setUrl(retrofitError.getUrl())
        return new ResponseEntity<Object>(networkErrorResponseModel, HttpStatus.INTERNAL_SERVER_ERROR)
      }
      if (retrofitError.getResponse()!=null && retrofitError.getResponse().getStatus() > 0){
        return new ResponseEntity<Object>(retrofitError.getBody(), HttpStatus.valueOf(retrofitError.getResponse().getStatus()))
      }
      return new ResponseEntity<Object>(retrofitError.getBody(), HttpStatus.INTERNAL_SERVER_ERROR)
    }
    return new ResponseEntity<Object>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR)
  }
}
