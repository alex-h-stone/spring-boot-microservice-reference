package com.cgi.example.petstore.integration.utils;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@Component
public class RequestURI {

    private final Environment environment;

    public RequestURI(Environment environment) {
        this.environment = environment;
    }

    public URI getApplicationURIFor(String resource) {
        return createURI(getApplicationPort(), resource);
    }

    public URI getManagementURIFor(String resource) {
        return createURI(getManagementPort(), resource);
    }

    private URI createURI(int portNumber, String resource) {
        String url = "http://localhost:" + portNumber + resource;

        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            String message = "Unable to create URI from [%s]".formatted(url);
            throw new RuntimeException(message, e);
        }
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
