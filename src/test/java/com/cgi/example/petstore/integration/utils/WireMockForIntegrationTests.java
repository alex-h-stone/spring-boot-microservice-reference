package com.cgi.example.petstore.integration.utils;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import com.cgi.example.petstore.embedded.WireMockEmbedded;
import com.cgi.example.petstore.local.SetSystemPropertiesForEmbeddedServices;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WireMockForIntegrationTests {

  // TODO integrate with WireMockEmbedded
  private final WireMockEmbedded wireMock;

  public WireMockForIntegrationTests() {
    wireMock = new WireMockEmbedded();
    wireMock.start();
    DynamicApplicationPropertiesRepository propertiesRepository =
        new DynamicApplicationPropertiesRepository();
    SetSystemPropertiesForEmbeddedServices.configureWireMock(propertiesRepository);
  }

  public void resetAll() {
    wireMock.getWireMockServer().resetAll();
  }

  public void stubFor(MappingBuilder mappingBuilder) {
    wireMock.getWireMockServer().stubFor(mappingBuilder);
  }

  public void verify(int numberOfTimes, RequestPatternBuilder request) {
    wireMock.getWireMockServer().verify(numberOfTimes, request);
  }

  public WireMockServer get() {
    return wireMock.getWireMockServer();
  }
}
