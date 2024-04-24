package com.cgi.example.petstore.local;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("local")
@Component
public class DynamicLocalConfiguration
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static final String VACCINATIONS_URL_SYSTEM_PROPERTY = "VACCINATIONS_URL";
  private static final String MONGO_DB_URI_SYSTEM_PROPERTY = "MONGO_DB_URI";

  private final DynamicApplicationPropertiesRepository propertiesRepository;

  public DynamicLocalConfiguration() {
    this(new DynamicApplicationPropertiesRepository());
  }

  // Constructor for injecting DynamicApplicationPropertiesRepository dependency to facilitate unit
  // testing
  public DynamicLocalConfiguration(
      DynamicApplicationPropertiesRepository dynamicApplicationPropertiesRepository) {
    this.propertiesRepository = dynamicApplicationPropertiesRepository;
  }

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    log.info("About to set Dynamic Local Configuration");

    setSystemPropertyIfAbsent(
        VACCINATIONS_URL_SYSTEM_PROPERTY,
        "http://localhost:" + propertiesRepository.getWireMockPort());

    setSystemPropertyIfAbsent(
        MONGO_DB_URI_SYSTEM_PROPERTY, propertiesRepository.getMongoDBConnectionString());

    log.info("Completed setting Dynamic Local Configuration");
  }

  private void setSystemPropertyIfAbsent(String systemPropertyKey, String systemPropertyValue) {
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
