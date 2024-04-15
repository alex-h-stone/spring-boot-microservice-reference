package com.cgi.example.petstore.local;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
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

  private final DynamicApplicationPropertiesRepository propertiesRepository =
      new DynamicApplicationPropertiesRepository();

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    log.debug("About to set local DynamicLocalConfiguration");

    System.setProperty(
        "VACCINATIONS_URL", "http://localhost:" + propertiesRepository.getWireMockPort());

    System.setProperty("MONGO_DB_URI", propertiesRepository.getMongoDBConnectionString());

    log.info("Completed setting local DynamicLocalConfiguration");
  }
}
