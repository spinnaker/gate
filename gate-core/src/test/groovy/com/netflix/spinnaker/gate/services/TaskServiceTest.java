/*
 * Copyright 2022 Netflix, Inc.
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

package com.netflix.spinnaker.gate.services;

import static org.mockito.Mockito.*;

import com.netflix.spinnaker.gate.services.internal.OrcaService;
import com.netflix.spinnaker.gate.services.internal.OrcaServiceSelector;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class TaskServiceTest {

  @Mock private OrcaServiceSelector selector;
  @Mock private OrcaService orcaService;

  @Test
  public void callAsManyTimesAsSet() {
    TaskService taskService = new TaskService(selector, null);
    Map operation = new LinkedHashMap();

    Map task = Map.of("ref", "apps/bob/someRandomId");
    when(selector.select()).thenReturn(orcaService);
    when(orcaService.doOperation(operation)).thenReturn(task);
    taskService.createAndWaitForCompletion(operation, 32, 1);
    verify(orcaService, times(32)).getTask("someRandomId");
  }
}
