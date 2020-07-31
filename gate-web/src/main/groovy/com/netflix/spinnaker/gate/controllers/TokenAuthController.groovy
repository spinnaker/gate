package com.netflix.spinnaker.gate.controllers

import com.netflix.spinnaker.gate.config.AuthenticationRequest
import com.netflix.spinnaker.gate.config.AuthenticationResponse
import com.netflix.spinnaker.gate.config.JwtUtil
import com.netflix.spinnaker.gate.services.UserDataService
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
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

    final String jwt = jwtTokenUtil.generateToken(userDetails);

    return ResponseEntity.ok(new AuthenticationResponse(jwt));
  }

}
