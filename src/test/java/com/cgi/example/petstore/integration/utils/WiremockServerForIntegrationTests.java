package com.cgi.example.petstore.integration.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.function.Supplier;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@Component
public class WiremockServerForIntegrationTests {
    private static final int WIREMOCK_PORT;
    private static final WireMockServer mockWebServer;

    static {
        final int nextAvailablePort = 0;
        try (var serverSocket = new ServerSocket(nextAvailablePort)) {
            WIREMOCK_PORT = serverSocket.getLocalPort();
        } catch (IOException e) {
            String message = "Unable to establish a WireMock port: [%s]".formatted(e.getMessage());
            throw new RuntimeException(message, e);
        }
        WireMock.configureFor(WIREMOCK_PORT); // Configure WireMock client
        mockWebServer = new WireMockServer(wireMockConfig().port(WIREMOCK_PORT));
        mockWebServer.start();
        waitUntil(mockWebServer::isRunning);
    }

    @Bean(destroyMethod = "shutdown")
    public WiremockServerForIntegrationTests stubServer() {
        return this;
    }

    void shutdown() {
        mockWebServer.shutdown();
        waitUntil(() -> !mockWebServer.isRunning());
    }

    public static int getPort() {
        return WIREMOCK_PORT;
    }

    public void resetAll() {
        mockWebServer.resetAll();
    }

    public void stubFor(MappingBuilder mappingBuilder) {
        mockWebServer.stubFor(mappingBuilder);
    }

    private static void waitUntil(Supplier<Boolean> isComplete) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        while (!isComplete.get() &&
                stopWatch.getTotalTimeSeconds() < 3.0) {
            // Loop until complete or timeout is reached
        }
        stopWatch.stop();
    }

    public void verify(int numberOfTimes, RequestPatternBuilder request) {
        mockWebServer.verify(numberOfTimes, request);
    }
}
