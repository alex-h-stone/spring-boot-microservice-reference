package com.cgi.example.petstore.integration.utils;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import com.cgi.example.petstore.embedded.EmbeddedMongoDB;
import com.cgi.example.petstore.local.SetSystemPropertiesForEmbeddedServices;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Component
public class MongoDbForIntegrationTests implements SmartLifecycle {

  private static final EmbeddedMongoDB MONGO_DB;

  static {
    MONGO_DB = new EmbeddedMongoDB();
    MONGO_DB.start();

    DynamicApplicationPropertiesRepository propertiesRepository =
        new DynamicApplicationPropertiesRepository();
    SetSystemPropertiesForEmbeddedServices.configureMongoDB(propertiesRepository);
  }

  @Override
  public void start() {
    MONGO_DB.start();
  }

  @Override
  public void stop() {
    MONGO_DB.stop();
  }

  @Override
  public boolean isRunning() {
    return MONGO_DB.isRunning();
  }

  @Override
  public int getPhase() {
    return Integer.MIN_VALUE;
  }
}
