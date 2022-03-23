
package com.netflix.spinnaker.gate.services

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

  static final String defaultGroup = "anonymous"

  @Override
  void loginWithRoles(String userId, Collection<String> roles) {
    if (fiatStatus.isEnabled()) {
        try {
          AuthenticatedRequest.allowAnonymous({
            fiatServiceForLogin.loginWithRoles(userId, roles)
            permissionEvaluator.invalidatePermission(userId)
          })
        } catch (RetrofitError e) {
          throw classifyError(e)
        }
    }
    if (isOesAuthorizationServiceEnabled){
      try {
        if (roles == null){
          roles = new ArrayList<>()
          roles.add(defaultGroup)
        } else if (!roles.isEmpty()){
          roles.add(defaultGroup)
        }
        oesAuthorizationService.cacheUserGroups(roles, userId)
      } catch(Exception e1){
        log.error("Exception occurred while login with roles : {}", e1)
      }
    }
  }
}
