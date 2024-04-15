package com.cgi.example.loadtest;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class HttpProtocolBuilders {

    private static final String APPLICATION_JSON = "application/json";
    private static final String HOSTNAME = "localhost";

    private static final int DEFAULT_APPLICATION_PORT = 8080;
    private static final int DEFAULT_MANAGEMENT_PORT = 8099;

    private final DynamicApplicationPropertiesRepository propertiesRepository = new DynamicApplicationPropertiesRepository();

    public HttpProtocolBuilder createApplicationProtocol() {
        Integer applicationPort = propertiesRepository.getApplicationPort();

        if (Objects.isNull(applicationPort)) {
            log.info("Unable to determine the applicationPort from DynamicApplicationProperties so defaulting to: {}", DEFAULT_APPLICATION_PORT);
            return createHttpProtocol(DEFAULT_APPLICATION_PORT);
        }

        return createHttpProtocol(applicationPort);
    }

    public HttpProtocolBuilder createManagementProtocol() {
        Integer managementPort = propertiesRepository.getManagementPort();

        if (Objects.isNull(managementPort)) {
            log.info("Unable to determine the managementPort from DynamicApplicationProperties so defaulting to: {}", DEFAULT_APPLICATION_PORT);
            return createHttpProtocol(DEFAULT_MANAGEMENT_PORT);
        }

        return createHttpProtocol(managementPort);
    }

    private HttpProtocolBuilder createHttpProtocol(int portNumber) {
        return HttpDsl.http.baseUrl("http://" + HOSTNAME + ":" + portNumber)
                .acceptHeader(APPLICATION_JSON)
                .disableCaching()
                .disableUrlEncoding()
                .contentTypeHeader(APPLICATION_JSON);
    }
}
