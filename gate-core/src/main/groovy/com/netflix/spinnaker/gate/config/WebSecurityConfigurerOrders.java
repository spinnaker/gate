/*
 * Copyright 2019 Netflix, Inc.
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
package com.netflix.spinnaker.gate.config;

public class WebSecurityConfigurerOrders {
  public static final int BASIC = 100;
  public static final int IAP = 200;
  public static final int LDAP = 300;
  public static final int OAUTH2 = 400;
  public static final int SAML = 500;
  public static final int X509 = 600;
  public static final int X509_STANDALONE = 601;
}
