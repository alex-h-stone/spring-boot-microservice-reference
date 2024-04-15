package com.cgi.example.petstore.integration.utils;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import com.cgi.example.petstore.embedded.EmbeddedWireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WireMockServerForIntegrationTests implements SmartLifecycle {

  private static final EmbeddedWireMockServer WIRE_MOCK_SERVER;

  static {
    WIRE_MOCK_SERVER = new EmbeddedWireMockServer();
    WIRE_MOCK_SERVER.start();

    DynamicApplicationPropertiesRepository propertiesRepository =
        new DynamicApplicationPropertiesRepository();
    System.setProperty(
        "VACCINATIONS_URL", "http://localhost:" + propertiesRepository.getWireMockPort());
  }

  public void resetAll() {
    WIRE_MOCK_SERVER.getWireMockServer().resetAll();
  }

  public void stubFor(MappingBuilder mappingBuilder) {
    WIRE_MOCK_SERVER.getWireMockServer().stubFor(mappingBuilder);
  }

  public void verify(int numberOfTimes, RequestPatternBuilder request) {
    WIRE_MOCK_SERVER.getWireMockServer().verify(numberOfTimes, request);
  }

  @Override
  public void start() {
    WIRE_MOCK_SERVER.start();
  }

  @Override
  public void stop() {
    WIRE_MOCK_SERVER.stop();
  }

  @Override
  public boolean isRunning() {
    return WIRE_MOCK_SERVER.isRunning();
  }

  @Override
  public int getPhase() {
    return Integer.MIN_VALUE;
  }
}
