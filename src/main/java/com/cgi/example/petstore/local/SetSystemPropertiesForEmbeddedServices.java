package com.cgi.example.petstore.local;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SetSystemPropertiesForEmbeddedServices {

  private static final String VACCINATIONS_URL = "VACCINATIONS_URL";
  private static final String MONGO_DB_URI = "MONGO_DB_URI";
  private static final String OAUTH_HOST = "OAUTH_HOST";

  public static void apply(DynamicApplicationPropertiesRepository propertiesRepository) {
    configureWireMock(propertiesRepository);
    configureMongoDB(propertiesRepository);
    configureOAuth2(propertiesRepository);
  }

  public static void configureMongoDB(DynamicApplicationPropertiesRepository propertiesRepository) {
    setSystemPropertyIfAbsent(MONGO_DB_URI, propertiesRepository.getMongoDBConnectionString());
  }

  public static void configureWireMock(
      DynamicApplicationPropertiesRepository propertiesRepository) {
    setSystemPropertyIfAbsent(
        VACCINATIONS_URL, "http://localhost:" + propertiesRepository.getWireMockPort());
  }

  public static void configureOAuth2(DynamicApplicationPropertiesRepository propertiesRepository) {
    setSystemPropertyIfAbsent(OAUTH_HOST, propertiesRepository.getOAuth2Host());
  }

  private static void setSystemPropertyIfAbsent(
      String systemPropertyKey, String systemPropertyValue) {
    String property = System.getProperty(systemPropertyKey);
    if (Objects.nonNull(property)) {
      log.info(
          "Not setting system property {} as it has already been set with a value of [{}]",
          systemPropertyKey,
          property);
    } else {
      System.setProperty(systemPropertyKey, systemPropertyValue);
    }
  }
}
