/*
 * Copyright 2022 Salesforce, Inc.
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

package com.netflix.spinnaker.gate.interceptors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.netflix.spinnaker.filters.AuthenticatedRequestFilter;
import com.netflix.spinnaker.gate.Main;
import com.netflix.spinnaker.gate.services.internal.Front50Service;
import com.netflix.spinnaker.kork.common.Header;
import com.netflix.spinnaker.security.AuthenticatedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = {Main.class, RequestIdInterceptorTest.TestController.class})
@TestPropertySource(properties = {"spring.config.location=classpath:gate-test.yml"})
public class RequestIdInterceptorTest {

  private static final String API_BASE = "/responseHeader";
  private static final String API_PATH = "/api";
  private static final String TEST_REQUEST_ID = "Test-Request-ID";

  @RestController
  @RequestMapping(value = API_BASE)
  public static class TestController {
    @RequestMapping(value = API_PATH, method = RequestMethod.GET)
    public void api() {}
  }

  private MockMvc mockMvc;

  @Autowired private WebApplicationContext webApplicationContext;

  private AuthenticatedRequestFilter authenticatedRequestFilter;

  @MockBean private Front50Service front50Service;

  @BeforeEach
  private void setup() {
    AuthenticatedRequest.clear();
    authenticatedRequestFilter = new AuthenticatedRequestFilter(true);
    mockMvc =
        webAppContextSetup(webApplicationContext).addFilters(authenticatedRequestFilter).build();
  }

  @Test
  public void testRequestIdExistsInAuthenticatedRequest() throws Exception {
    AuthenticatedRequest.setRequestId(TEST_REQUEST_ID);

    mockMvc
        .perform(get(API_BASE + API_PATH))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().exists(Header.REQUEST_ID.getHeader()))
        .andExpect(header().string(Header.REQUEST_ID.getHeader(), equalTo(TEST_REQUEST_ID)));
  }

  @Test
  public void testNoHeaderFieldsInAuthenticatedRequest() throws Exception {
    // AuthenticatedRequest generates an uuid as request id if none exists
    // so there will always be a request id in the response header if the
    // interceptor is enabled
    mockMvc
        .perform(get(API_BASE + API_PATH))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(header().exists(Header.REQUEST_ID.getHeader()))
        .andExpect(header().string(Header.REQUEST_ID.getHeader(), notNullValue()));
  }
}
