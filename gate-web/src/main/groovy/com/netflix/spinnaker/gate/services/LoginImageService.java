/*
 * Copyright 2019 OpsMX, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import groovy.transform.CompileStatic;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@CompileStatic
@Component
public class LoginImageService {
  private static final Logger log = LoggerFactory.getLogger(LoginImageService.class);

  public void saveImage(String imageUrl, String destinationFile) {
    try {
      URL url = new URL(imageUrl);
      InputStream is = url.openStream();
      OutputStream os = new FileOutputStream(destinationFile);

      byte[] b = new byte[2048];
      int length;

      while ((length = is.read(b)) != -1) {
        os.write(b, 0, length);
      }

      is.close();
      os.close();
    } catch (IOException e) {
      log.info("the image is not found or failed to save :" + imageUrl);
    }
  }
}
