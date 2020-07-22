package com.netflix.spinnaker.gate;

import com.netflix.spinnaker.kork.retrofit.RetrofitServiceProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {Main.class})
@ActiveProfiles("test")
@TestPropertySource(properties = {"spring.config.location=classpath:gate-test.yml"})
public class MainSpec {

  @Autowired RetrofitServiceProvider retrofitServiceProvider;

  @Test
  public void startupTest() {
    assert retrofitServiceProvider != null;
  }
}
