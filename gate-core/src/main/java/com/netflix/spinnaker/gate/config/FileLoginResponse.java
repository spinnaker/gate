package com.netflix.spinnaker.gate.config;

import java.io.Serializable;

public class FileLoginResponse implements Serializable {

  private String username;
  private String password;
  private boolean validUser;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isValidUser() {
    return validUser;
  }

  public void setValidUser(boolean validUser) {
    this.validUser = validUser;
  }

  public FileLoginResponse() {}

  public FileLoginResponse(String username, String password, boolean validUser) {
    this.setUsername(username);
    this.setPassword(password);
    this.setValidUser(validUser);
  }
}
