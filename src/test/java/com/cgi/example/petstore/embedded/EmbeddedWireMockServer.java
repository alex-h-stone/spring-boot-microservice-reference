package com.cgi.example.petstore.embedded;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import com.cgi.example.common.local.model.ApplicationModule;
import com.cgi.example.petstore.utils.ProcessManagement;
import com.cgi.example.petstore.utils.ResourceFileUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.trafficlistener.ConsoleNotifyingWiremockNetworkTrafficListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Slf4j
public class EmbeddedWireMockServer implements ManageableService {

  private final DynamicApplicationPropertiesRepository propertiesRepository =
      new DynamicApplicationPropertiesRepository();
  private final String defaultResponse;
  @Getter private final WireMockServer wireMockServer;

  /**
   * Used to start the WireMockServer independently of integration tests. e.g. If running the
   * microservice locally you may want to stub external API calls using WireMock.
   */
  public static void main(String[] args) {
    ManageableService wireMockServer = new EmbeddedWireMockServer();
    wireMockServer.start();
  }

  public EmbeddedWireMockServer() {
    ResourceFileUtils resourceFileUtils = new ResourceFileUtils();
    this.defaultResponse =
        resourceFileUtils.readFile(
            "external\\animalvaccinationapi\\response\\vaccinationResponseMultiple.json");

    WireMockConfiguration wireMockConfiguration =
        options()
            .dynamicPort()
            .notifier(new ConsoleNotifier("WireMockConsoleLog", true))
            .networkTrafficListener(new ConsoleNotifyingWiremockNetworkTrafficListener());
    wireMockServer = new WireMockServer(wireMockConfiguration);
  }

  @Override
  public void start() {
    if (isRunning()) {
      log.debug("Cannot start Embedded WireMock as it is already running");
      return;
    }

    log.info("Configuring Embedded WireMock stub responses");
    wireMockServer.stubFor(
        WireMock.get(urlPathMatching("/vaccinations/.*"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(defaultResponse)
                    .withStatus(HttpStatus.OK.value())));

    log.info("Starting Embedded WireMockServer");
    wireMockServer.start();
    ProcessManagement.waitUntil(wireMockServer::isRunning);

    int wireMockPort = wireMockServer.port();
    log.info("Started Embedded WireMockServer on port: {}", wireMockPort);

    propertiesRepository.setWireMockPort(ApplicationModule.APPLICATION_TEST, wireMockPort);
  }

  @Override
  public void stop() {
    if (!isRunning()) {
      log.debug("Cannot stop Embedded WireMock as it has already stopped");
      return;
    }

    log.info("Shutting down Embedded WireMock");
    wireMockServer.stop();
    ProcessManagement.waitUntil(() -> !isRunning());
    log.info("Embedded WireMock has shut down");
  }

  @Override
  public boolean isRunning() {
    return wireMockServer.isRunning();
  }
}
