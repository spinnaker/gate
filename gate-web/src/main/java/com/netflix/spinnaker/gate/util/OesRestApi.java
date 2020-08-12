package com.netflix.spinnaker.gate.util;

import java.io.IOException;
import okhttp3.*;

public class OesRestApi {
  private static OkHttpClient httpClient = new OkHttpClient();

  public static boolean initiateUserGroupInPlatform(String url) throws IOException {
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(JSON, "");
    Request request = new Request.Builder().url(url).put(body).build();

    try (Response response = httpClient.newCall(request).execute()) {
      return response.isSuccessful();
    }
  }
}
