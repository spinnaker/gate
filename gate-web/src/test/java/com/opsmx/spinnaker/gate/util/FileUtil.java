/*
 * Copyright 2021 OpsMx, Inc.
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

package com.opsmx.spinnaker.gate.util;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import org.springframework.core.io.ClassPathResource;

public interface FileUtil {

  Gson gson = new Gson();

  static InputStream getFileStream(String filePath) throws IOException {
    return new ClassPathResource(filePath).getInputStream();
  }

  static <T> T getFileAsJsonObject(String filePath, Class<T> typeOfSrc) throws Exception {
    try (InputStream is = getFileStream(filePath);
        Reader reader = new InputStreamReader(is, "UTF-8")) {
      return gson.fromJson(reader, typeOfSrc);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }
}
