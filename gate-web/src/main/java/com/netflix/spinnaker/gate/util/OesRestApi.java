package com.netflix.spinnaker.gate.util;

import com.google.gson.Gson;
import com.netflix.spinnaker.gate.config.AuthenticationRequest;
import com.netflix.spinnaker.gate.config.FileLoginResponse;
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

  public static boolean initiateFileLoginInPlatform(
      String url, AuthenticationRequest authenticationRequest) throws IOException {
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Gson gson = new Gson();
    String data = gson.toJson(authenticationRequest);
    RequestBody body = RequestBody.create(JSON, data);
    Request request = new Request.Builder().url(url).post(body).build();
    boolean isValidUser = false;
    try (Response response = httpClient.newCall(request).execute()) {
      if (response.isSuccessful()) {
        FileLoginResponse entity = gson.fromJson(response.body().string(), FileLoginResponse.class);
        isValidUser = entity.isValidUser();
      }
    }
    return isValidUser;
  }
}
