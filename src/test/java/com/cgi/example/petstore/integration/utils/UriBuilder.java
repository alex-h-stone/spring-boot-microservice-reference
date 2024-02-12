package com.cgi.example.petstore.integration.utils;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Component
public class UriBuilder {

    public static final String PET_STORE_BASE_URL = "api/v1/pet-store/pets";

    private final Environment environment;

    public UriBuilder(Environment environment) {
        this.environment = environment;
    }

    public UriComponentsBuilder getPetStoreURIFor(String resource) {
        return getApplicationURIFor(PET_STORE_BASE_URL)
                .pathSegment(resource);
    }

    public UriComponentsBuilder getPetStoreBaseURI() {
        return getApplicationURIFor(PET_STORE_BASE_URL);
    }

    public UriComponentsBuilder getApplicationURIFor(String resource) {
        return getUriComponentsBuilder(getApplicationPort())
                .pathSegment(resource);
    }

    public UriComponentsBuilder getManagementURIFor(String resource) {
        return getUriComponentsBuilder(getManagementPort())
                .pathSegment(resource);
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

    private UriComponentsBuilder getUriComponentsBuilder(int port) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
        uriComponentsBuilder.scheme("http")
                .host("localhost")
                .port(port);

        return uriComponentsBuilder;
    }
}
