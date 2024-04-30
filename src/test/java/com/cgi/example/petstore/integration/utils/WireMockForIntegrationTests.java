package com.cgi.example.petstore.integration.utils;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import com.cgi.example.petstore.embedded.EmbeddedWireMock;
import com.cgi.example.petstore.local.SetSystemPropertiesForEmbeddedServices;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WireMockForIntegrationTests
    implements SmartLifecycle { // TODO do we need SmartLifecycle

  // TODO integrate with EmbeddedWireMock
  private static final EmbeddedWireMock WIRE_MOCK;

  static {
    WIRE_MOCK = new EmbeddedWireMock();
    WIRE_MOCK.start();
    DynamicApplicationPropertiesRepository propertiesRepository =
        new DynamicApplicationPropertiesRepository();
    SetSystemPropertiesForEmbeddedServices.configureWireMock(propertiesRepository);
  }

  public void resetAll() {
    WIRE_MOCK.getWireMockServer().resetAll();
  }

  public void stubFor(MappingBuilder mappingBuilder) {
    WIRE_MOCK.getWireMockServer().stubFor(mappingBuilder);
  }

  public void verify(int numberOfTimes, RequestPatternBuilder request) {
    WIRE_MOCK.getWireMockServer().verify(numberOfTimes, request);
  }

  public WireMockServer get() {
    return WIRE_MOCK.getWireMockServer();
  }

  @Override
  public void start() {
    WIRE_MOCK.start();
  }

  @Override
  public void stop() {
    WIRE_MOCK.stop();
  }

  @Override
  public boolean isRunning() {
    return WIRE_MOCK.isRunning();
  }

  @Override
  public int getPhase() {
    return Integer.MIN_VALUE;
  }
}
