package com.cgi.example.petstore.local;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import com.cgi.example.petstore.config.PetStoreSystemProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetSystemPropertiesForEmbeddedServices {

  private static final String DUMMY_CLIENT_ID = "DummyClientId";
  private static final String DUMMY_CLIENT_SECRET = "DummyClientSecret";
  private static final String WIRE_MOCK_HOST_FORMAT = "http://localhost:%d";

  public void apply(DynamicApplicationPropertiesRepository propertiesRepository) {
    configureWireMock(propertiesRepository);
    configureMongoDB(propertiesRepository);
    configureOAuth2(propertiesRepository);
  }

  private void configureMongoDB(DynamicApplicationPropertiesRepository propertiesRepository) {
    String mongoDBConnectionString = propertiesRepository.getMongoDBConnectionString();
    PetStoreSystemProperty.MONGO_DB_URI.setSystemPropertyIfAbsent(mongoDBConnectionString);
  }

  private void configureWireMock(DynamicApplicationPropertiesRepository propertiesRepository) {
    String newSystemPropertyValue =
        WIRE_MOCK_HOST_FORMAT.formatted(propertiesRepository.getWireMockPort());
    PetStoreSystemProperty.VACCINATIONS_URL.setSystemPropertyIfAbsent(newSystemPropertyValue);
  }

  private void configureOAuth2(DynamicApplicationPropertiesRepository propertiesRepository) {
    String oAuth2Host = propertiesRepository.getOAuth2Host();
    PetStoreSystemProperty.OAUTH_HOST.setSystemPropertyIfAbsent(oAuth2Host);

    PetStoreSystemProperty.OAUTH_CLIENT_ID.setSystemPropertyIfAbsent(DUMMY_CLIENT_ID);
    PetStoreSystemProperty.OAUTH_CLIENT_SECRET.setSystemPropertyIfAbsent(DUMMY_CLIENT_SECRET);
  }
}
