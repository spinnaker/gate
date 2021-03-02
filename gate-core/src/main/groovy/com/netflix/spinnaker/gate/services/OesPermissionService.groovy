
package com.netflix.spinnaker.gate.services

import com.netflix.spinnaker.gate.services.commands.HystrixFactory
import com.netflix.spinnaker.security.AuthenticatedRequest
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import retrofit.RetrofitError

import static com.netflix.spinnaker.gate.retrofit.UpstreamBadRequest.classifyError

@Slf4j
@Primary
@Service
class OesPermissionService extends PermissionService{

  @Value('${services.platform.enabled}')
  boolean isOesAuthorizationServiceEnabled

  @Autowired
  OesAuthorizationService oesAuthorizationService

  @Override
  void loginWithRoles(String userId, Collection<String> roles) {
    if (fiatStatus.isEnabled()) {
      HystrixFactory.newVoidCommand(HYSTRIX_GROUP, "loginWithRoles") {
        try {
          AuthenticatedRequest.allowAnonymous({
            fiatServiceForLogin.loginWithRoles(userId, roles)
            permissionEvaluator.invalidatePermission(userId)
          })
        } catch (RetrofitError e) {
          throw classifyError(e)
        }
      }.execute()
    }
    if (isOesAuthorizationServiceEnabled){
      try {
        oesAuthorizationService.cacheUserGroups(roles, userId)
      } catch(Exception e1){
        log.error("Exception occured while login with roles : {}", e1)
      }
    }
  }
}
