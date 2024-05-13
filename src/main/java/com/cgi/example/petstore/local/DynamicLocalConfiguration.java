package com.cgi.example.petstore.local;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("local")
@Order(1)
@Component
public class DynamicLocalConfiguration
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private final DynamicApplicationPropertiesRepository propertiesRepository;

  public DynamicLocalConfiguration() {
    propertiesRepository = new DynamicApplicationPropertiesRepository();
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

    SetSystemPropertiesForEmbeddedServices embeddedServices =
        new SetSystemPropertiesForEmbeddedServices();
    embeddedServices.apply(propertiesRepository);

    log.info("Completed setting Dynamic Local Configuration");
  }
}
