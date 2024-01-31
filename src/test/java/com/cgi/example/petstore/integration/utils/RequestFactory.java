package com.cgi.example.petstore.integration.utils;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Component
public class RequestFactory {

    private final Environment environment;

    public RequestFactory(Environment environment) {
        this.environment = environment;
    }

    public RequestEntity<String> createManagementRequest(HttpMethod httpMethod, String resource) {
        return createRequestEntity(getManagementPort(), resource, httpMethod);
    }

    public RequestEntity<String> createApplicationRequest(HttpMethod httpMethod, String resource) {
        return createRequestEntity(getApplicationPort(), resource, httpMethod);
    }

    private RequestEntity<String> createRequestEntity(int portNumber,
                                                      String resource,
                                                      HttpMethod httpMethod) {
        String url = "http://localhost:" + portNumber + resource;
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            String message = "Unable to create URI from [%s]".formatted(url);
            throw new RuntimeException(message, e);
        }

        return new RequestEntity<>(httpMethod, uri);
    }

    private int getManagementPort() {
        return getPositiveIntegerEnvironmentProperty("local.management.port",
                "Unable to determine the management port [local.management.port] this should have been set as the next available port, but instead found [%d]");
    }

    private int getApplicationPort() {
        return getPositiveIntegerEnvironmentProperty("local.server.port",
                "Unable to determine the application port [local.server.port] this should have been set as the next available port, but instead found [%d]");
    }

    private int getPositiveIntegerEnvironmentProperty(String environmentPropertyKey, String message) {
        Integer integerEnvironmentProperty = environment.getProperty(environmentPropertyKey, Integer.class);

        if (Objects.isNull(integerEnvironmentProperty) || integerEnvironmentProperty <= 0) {
            throw new IllegalStateException(message.formatted(integerEnvironmentProperty));
        }

        return integerEnvironmentProperty;
    }
}
