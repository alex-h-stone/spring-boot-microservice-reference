package com.cgi.example.petstore;

import com.cgi.example.petstore.utils.ResourceFileUtils;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.trafficlistener.ConsoleNotifyingWiremockNetworkTrafficListener;
import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class VaccinationsWireMockServer {

    private final int portNumber;
    private final String defaultResponse;

    public static void main(String[] args) {
        int portNumberParameter = extractPortNumberFrom(args);
        ResourceFileUtils resourceFileUtils = new ResourceFileUtils();
        String defaultVaccinationsResponse = resourceFileUtils.readFile("thirdparty\\animalvaccinationapi\\response\\vaccinationResponseMultiple.json");

        System.out.println("Instantiating WireMockServer");
        VaccinationsWireMockServer wireMockServer = new VaccinationsWireMockServer(portNumberParameter, defaultVaccinationsResponse);

        wireMockServer.start();
    }

    private static int extractPortNumberFrom(String[] args) {
        Validate.notNull(args, "Expected a port number but found null args [" + Arrays.toString(args) + "]");
        Validate.notEmpty(args, "Expected a port number but found empty args [" + Arrays.toString(args) + "]");

        return Integer.parseInt(args[0]);
    }

    public VaccinationsWireMockServer(int portNumber, String defaultResponse) {
        this.portNumber = portNumber;
        this.defaultResponse = defaultResponse;
    }

    private void start() {
        System.out.println("Instantiating WireMockConfiguration with portNumber: " + portNumber);
        WireMockConfiguration wireMockConfiguration = options().port(portNumber)
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
