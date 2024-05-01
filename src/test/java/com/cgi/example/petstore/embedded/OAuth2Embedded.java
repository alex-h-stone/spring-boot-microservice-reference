package com.cgi.example.petstore.embedded;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import no.nav.security.mock.oauth2.MockOAuth2Server;
import no.nav.security.mock.oauth2.OAuth2Config;

@Slf4j
public class OAuth2Embedded {

  private final DynamicApplicationPropertiesRepository propertiesRepository =
      new DynamicApplicationPropertiesRepository();
  private MockOAuth2Server mockOAuth2Server;

  public static void main(String[] args) {
    new OAuth2Embedded();
  }

  public OAuth2Embedded() {
    start();
  }

  public void start() {
    OAuth2Config config = new OAuth2Config(false);
    mockOAuth2Server = new MockOAuth2Server(config);

    log.info("Starting Embedded OAuth2");
    mockOAuth2Server.start();
    int port = mockOAuth2Server.getConfig().getHttpServer().port();
    log.info("Started Embedded OAuth2 on port: {}", port);

    propertiesRepository.setOAuth2Port(getClass(), port);
  }

  public void stop() {
    mockOAuth2Server.shutdown();
    mockOAuth2Server = null;
  }

  public boolean isRunning() {
    return Objects.nonNull(mockOAuth2Server);
  }

  public String issueToken() {
    String issuerId = propertiesRepository.getOAuth2Host() + "/default";

    return mockOAuth2Server.issueToken(issuerId, "DummyClientId", "petStoreAPI").serialize();
  }
}
