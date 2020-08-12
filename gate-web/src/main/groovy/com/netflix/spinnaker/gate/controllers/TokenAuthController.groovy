package com.netflix.spinnaker.gate.controllers

import com.netflix.spinnaker.gate.config.AuthenticationRequest
import com.netflix.spinnaker.gate.config.AuthenticationResponse
import com.netflix.spinnaker.gate.config.JwtUtil
import com.netflix.spinnaker.gate.services.UserDataService
import com.netflix.spinnaker.gate.util.OesRestApi
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@ConditionalOnExpression('${ldap.enabled:false}')
@RestController
@RequestMapping("/auth")
class TokenAuthController {
  @Autowired
  private JwtUtil jwtTokenUtil

  @Autowired
  UserDataService userDetailsService

  @Autowired
  AuthenticationManager authenticationManager

  @Value('${services.platform.enabled:false}')
  boolean isPlatformEnabled;

  @Value('${services.platform.baseUrl:null}')
  String url;

  @Value('${services.platform.groupPath:null}')
  String apiPath;

  @ApiOperation(value = "New Login for Jwt")
  @RequestMapping(value = "/login", method = RequestMethod.POST)
  public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest authenticationRequest ) {

    try {
      authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
      );
    }
    catch (BadCredentialsException e) {
      throw new Exception("Incorrect username or password", e);
    }

    final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

    if (isPlatformEnabled) {
      String path = apiPath.replace("{username}",userDetails.getUsername());
      boolean isSuccessful = OesRestApi.initiateUserGroupInPlatform(url+path);
      if (isSuccessful) {
        final String jwt = jwtTokenUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
      }
      else {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }
    else {
      final String jwt = jwtTokenUtil.generateToken(userDetails);
      return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
  }

}
