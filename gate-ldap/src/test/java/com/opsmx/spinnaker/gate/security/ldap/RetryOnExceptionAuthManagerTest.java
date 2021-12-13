package com.opsmx.spinnaker.gate.security.ldap;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

public class RetryOnExceptionAuthManagerTest {

  private AuthenticationManager delegate = mock(AuthenticationManager.class);

  private Authentication authentication = mock(Authentication.class);

  private RetryOnExceptionAuthManager fixture = new RetryOnExceptionAuthManager(delegate);

  @Test
  @DisplayName("test exception case")
  public void test1() {

    Mockito.when(delegate.authenticate(authentication))
        .thenThrow(new UncategorizedLdapException(""));

    try {
      fixture.authenticate(authentication);
      fail("Should fail when try attempts are finished");
    } catch (UncategorizedLdapException e) {

    } catch (Exception e) {
      fail("UncategorizedLdapException is expected");
    }
  }

  @Test
  @DisplayName("test when no exception")
  public void test2() {

    Mockito.when(delegate.authenticate(authentication)).thenReturn(authentication);

    Authentication actual = fixture.authenticate(authentication);

    Assert.assertEquals(actual, authentication);
  }
}
