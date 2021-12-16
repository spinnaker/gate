package com.opsmx.spinnaker.gate.security.ldap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Retry authentication in cases of transient issues.
 *
 * @author Rahulgandhi Chekuri
 */
@Slf4j
public class RetryOnExceptionAuthManager implements AuthenticationManager {

  public static final int DEFAULT_RETRIES = 3;
  public static final long DEFAULT_TIME_TO_WAIT_MS = 2000;

  private int numRetries;
  private long timeToWaitMS;
  private AuthenticationManager delegate;

  // CONSTRUCTORS
  public RetryOnExceptionAuthManager(
      int _numRetries, long _timeToWaitMS, AuthenticationManager delegate) {
    this.numRetries = _numRetries;
    this.timeToWaitMS = _timeToWaitMS;
    this.delegate = delegate;
  }

  public RetryOnExceptionAuthManager(AuthenticationManager delegate) {
    this(DEFAULT_RETRIES, DEFAULT_TIME_TO_WAIT_MS, delegate);
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Authentication auth = null;
    while (true) {
      try {
        auth = delegate.authenticate(authentication);
        log.info("Authenticated without connection issue");
        break;
      } catch (UncategorizedLdapException ldapException) {
        exceptionOccurred(ldapException);
        continue;
      }
    }

    return auth;
  }

  /*
   * shouldRetry
   * Returns true if a retry can be attempted.
   * @return  True if retries attempts remain; else false
   */
  private boolean shouldRetry() {
    return (numRetries >= 0);
  }

  /*
   * waitUntilNextTry
   * Waits for timeToWaitMS. Ignores any interrupted exception
   */
  private void waitUntilNextTry() {
    try {
      Thread.sleep(timeToWaitMS);
    } catch (InterruptedException iex) {
    }
  }

  /*
   * exceptionOccurred
   * Call when an exception has occurred in the block. If the
   * retry limit is exceeded, throws an exception.
   * Else waits for the specified time.
   * @throws Exception
   * @param e
   */
  private void exceptionOccurred(UncategorizedLdapException e)
      throws LDAPConnectionClosedException {
    numRetries--;
    if (!shouldRetry()) {
      throw new LDAPConnectionClosedException(e.getMessage());
    } else {
      log.info("Ldap exception occurred so retrying the authentication, The exception is : {}", e);
    }
    waitUntilNextTry();
  }
}
