package com.cgi.example.petstore;

import com.cgi.example.petstore.utils.ResourceFileUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.trafficlistener.ConsoleNotifyingWiremockNetworkTrafficListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Objects;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class VaccinationsWireMockServer {

    private static final int DEFAULT_PORT_NUMBER = 8081;

    private final int portNumber;
    private final String defaultResponse;

    /**
     * Used to start the WireMockServer independently of integration tests.
     * e.g. If running the microservice locally you may want to stub external API calls using WireMock.
     *
     * @param args portNumber (defaults to 8081 if not provided)
     */
    public static void main(String[] args) {
        int portNumber = getPortNumberElseDefault(args);
        ResourceFileUtils resourceFileUtils = new ResourceFileUtils();
        String defaultVaccinationsResponse = resourceFileUtils.readFile("external\\animalvaccinationapi\\response\\vaccinationResponseMultiple.json");

        System.out.println("Instantiating WireMockServer");
        VaccinationsWireMockServer wireMockServer = new VaccinationsWireMockServer(portNumber, defaultVaccinationsResponse);

        wireMockServer.start();
    }

    private static int getPortNumberElseDefault(String[] args) {
        if (Objects.isNull(args) || args.length == 0) {
            return DEFAULT_PORT_NUMBER;
        }

        return Integer.parseInt(args[0]);
    }

    private VaccinationsWireMockServer(int portNumber, String defaultResponse) {
        this.portNumber = portNumber;
        this.defaultResponse = defaultResponse;
    }

    private void start() {
        System.out.println("Instantiating WireMockConfiguration with portNumber: " + portNumber);

        WireMockConfiguration wireMockConfiguration = options()
                .port(portNumber)
                .notifier(new ConsoleNotifier("WireMockConsoleLog", true))
                .networkTrafficListener(new ConsoleNotifyingWiremockNetworkTrafficListener());

        WireMockServer wireMockServer = new WireMockServer(wireMockConfiguration);

        System.out.println("Configuring WireMock stub responses");
        wireMockServer.stubFor(WireMock.get(urlPathMatching("/vaccinations/.*"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(defaultResponse)
                        .withStatus(HttpStatus.OK.value())));

        System.out.println("Starting WireMockServer");
        wireMockServer.start();
    }
}
