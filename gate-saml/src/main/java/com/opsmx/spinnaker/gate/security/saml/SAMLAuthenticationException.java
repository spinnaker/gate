package com.opsmx.spinnaker.gate.security.saml;

import org.springframework.security.core.AuthenticationException;

public class SAMLAuthenticationException extends AuthenticationException {
  public SAMLAuthenticationException(String msg) {
    super(msg);
  }

  public SAMLAuthenticationException(String msg, Throwable t) {
    super(msg, t);
  }
}
