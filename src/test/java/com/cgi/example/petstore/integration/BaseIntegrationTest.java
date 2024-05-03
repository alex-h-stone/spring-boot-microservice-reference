package com.cgi.example.petstore.integration;

import com.cgi.example.petstore.PetStoreApplication;
import com.cgi.example.petstore.embedded.OAuth2Embedded;
import com.cgi.example.petstore.embedded.WireMockEmbedded;
import com.cgi.example.petstore.integration.utils.UriBuilder;
import com.cgi.example.petstore.integration.utils.WebClientExecutor;
import com.cgi.example.petstore.utils.AssertionExecutables;
import com.cgi.example.petstore.utils.ResourceFileUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = {PetStoreApplication.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"management.server.port=0", "de.flapdoodle.mongodb.embedded.version=4.4.18"})
@Tag("integration")
@ActiveProfiles("local")
public abstract class BaseIntegrationTest {

  private static final WireMockEmbedded WIRE_MOCK = new WireMockEmbedded();
  private static final OAuth2Embedded O_AUTH_2 = new OAuth2Embedded();

  protected final AssertionExecutables assertions = new AssertionExecutables();
  protected final ResourceFileUtils fileUtils = new ResourceFileUtils();

  @Autowired private MongoTemplate mongoTemplate;

  @Autowired protected WebClientExecutor webClientExecutor;

  @Autowired protected UriBuilder uriBuilder;

  @BeforeEach
  void beforeEach() {
    WIRE_MOCK.resetAll();
    dropAllMongoDBCollections();
  }

  @AfterEach
  void afterEach() {
    WIRE_MOCK.resetAll();
    dropAllMongoDBCollections();
  }

  public WireMockServer wireMock() {
    return WIRE_MOCK.getWireMockServer();
  }

  private void dropAllMongoDBCollections() {
    Set<String> collectionNames = mongoTemplate.getCollectionNames();
    collectionNames.forEach(mongoTemplate::dropCollection);
  }

  // TODO does this belong here?
  public static String getOAuth2AuthorizationHeader() {
    return "Bearer " + O_AUTH_2.issueToken();
  }
}
