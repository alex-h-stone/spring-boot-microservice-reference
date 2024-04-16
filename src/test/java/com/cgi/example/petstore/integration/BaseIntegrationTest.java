package com.cgi.example.petstore.integration;

import com.cgi.example.petstore.integration.utils.UriBuilder;
import com.cgi.example.petstore.integration.utils.WebClientExecutor;
import com.cgi.example.petstore.integration.utils.WireMockServerForIntegrationTests;
import com.cgi.example.petstore.utils.AssertionExecutables;
import com.cgi.example.petstore.utils.ResourceFileUtils;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"management.server.port=0", "de.flapdoodle.mongodb.embedded.version=4.4.18"})
@Tag("integration")
public abstract class BaseIntegrationTest {

  protected final AssertionExecutables assertions = new AssertionExecutables();
  protected final ResourceFileUtils fileUtils = new ResourceFileUtils();

  @Autowired private MongoTemplate mongoTemplate;

  @Autowired protected WebClientExecutor testRestTemplate;

  @Autowired protected UriBuilder uriBuilder;

  @Autowired protected WireMockServerForIntegrationTests stubServer;

  private void dropAllMongoDBCollections() {
    Set<String> collectionNames = mongoTemplate.getCollectionNames();
    collectionNames.forEach(mongoTemplate::dropCollection);
  }

  @BeforeEach
  void beforeEach() {
    stubServer.resetAll();
    dropAllMongoDBCollections();
  }

  @AfterEach
  void afterEach() {
    stubServer.resetAll();
    dropAllMongoDBCollections();
  }
}
