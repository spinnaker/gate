package com.netflix.spinnaker.gate.security.saml;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.saml2.core.Saml2X509Credential;

class SecuritySamlPropertiesTest {

  @Test
  public void verifyCanLoadCerts() {
    SecuritySamlProperties properties = new SecuritySamlProperties();
    properties.setSigningCredentials(
        List.of(
            new SecuritySamlProperties.Credential(
                "classpath:private_key.pem", "classpath:certificate.pem")));
    List<Saml2X509Credential> signingCredentials = properties.getSigningCredentials();
    assertThat(signingCredentials.get(0).getPrivateKey().getAlgorithm()).isEqualTo("RSA");
  }

  @Test
  public void verifyCanLoadCertsFromAFileLocation() {
    SecuritySamlProperties properties = new SecuritySamlProperties();
    Path currentDir = Paths.get("");
    properties.setSigningCredentials(
        List.of(
            new SecuritySamlProperties.Credential(
                currentDir.toAbsolutePath() + "/src/test/resources/private_key.pem",
                currentDir.toAbsolutePath() + "/src/test/resources/certificate.pem")));
    List<Saml2X509Credential> signingCredentials = properties.getSigningCredentials();
    assertThat(signingCredentials.get(0).getPrivateKey().getAlgorithm()).isEqualTo("RSA");
  }
}
