package com.cgi.example.common.local;

import com.cgi.example.common.DynamicApplicationFileProperties;
import com.cgi.example.common.local.model.ApplicationModule;
import com.cgi.example.common.local.model.DynamicApplicationProperties;
import com.cgi.example.common.local.model.Port;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Objects;

@Slf4j
public class DynamicApplicationPropertiesRepository {

    private final ObjectMapper objectMapper;
    private final ToClickableUriString toClickableUriString = new ToClickableUriString();
    private final Path pathToApplicationProperties;

    public DynamicApplicationPropertiesRepository() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        pathToApplicationProperties = Path.of(DynamicApplicationFileProperties.FILE_PATH);
    }

    public Integer getWireMockPort() {
        return portNumberOrNull(retrieve().getWireMockPort());
    }

    public Integer getApplicationPort() {
        return portNumberOrNull(retrieve().getApplicationPort());
    }

    public Integer getManagementPort() {
        return portNumberOrNull(retrieve().getManagementPort());
    }

    public String getMongoDBConnectionString() {
        return String.format("mongodb://localhost:%d", portNumberOrNull(retrieve().getMongoDBPort()));
    }

    public void setApplicationPort(ApplicationModule applicationModule, int applicationPortNumber) {
        DynamicApplicationProperties applicationProperties = retrieve();
        applicationProperties.setApplicationPort(createPort(applicationModule, applicationPortNumber));
        save(applicationProperties);
    }

    public void setManagementPort(ApplicationModule applicationModule, int managementPortNumber) {
        DynamicApplicationProperties applicationProperties = retrieve();
        applicationProperties.setManagementPort(createPort(applicationModule, managementPortNumber));
        save(applicationProperties);
    }

    public void setWireMockPort(ApplicationModule applicationModule, int wireMockPort) {
        DynamicApplicationProperties applicationProperties = retrieve();
        applicationProperties.setWireMockPort(createPort(applicationModule, wireMockPort));
        save(applicationProperties);
    }

    public void setMongoDBPort(ApplicationModule applicationModule, int mongoDBPort) {
        DynamicApplicationProperties applicationProperties = retrieve();
        applicationProperties.setMongoDBPort(createPort(applicationModule, mongoDBPort));
        save(applicationProperties);
    }

    private Integer portNumberOrNull(Port port) {
        return Objects.isNull(port) ? null : port.getPort();
    }

    @Nonnull
    private DynamicApplicationProperties retrieve() {
        File file = pathToApplicationProperties.toFile();
        if (!file.exists()) {
            log.info("Given the the file path [{}] unable to find: {}", file.getAbsolutePath(), toClickableUriString.apply(file));
            return new DynamicApplicationProperties();
        }

        return readApplicationPropertiesFrom(pathToApplicationProperties);
    }

    private void save(DynamicApplicationProperties applicationProperties) {
        try {
            String dynamicApplicationPropertiesJson =
                    objectMapper.writeValueAsString(applicationProperties);
            log.info("About to save dynamicApplicationProperties: {} to: {}",
                    applicationProperties, toClickableUriString.apply(pathToApplicationProperties.toFile()));

            Files.createDirectories(pathToApplicationProperties.getParent());
            Files.writeString(
                    pathToApplicationProperties,
                    dynamicApplicationPropertiesJson,
                    StandardCharsets.UTF_8);
            log.info("Successfully saved dynamicApplicationProperties: {}", toClickableUriString.apply(pathToApplicationProperties.toFile()));
        } catch (JsonProcessingException e) {
            log.warn("Unable to deserialise dynamicApplicationProperties: {}", e.getMessage(), e);
        } catch (IOException e) {
            log.warn("Unable to save dynamicApplicationProperties to disk: {}", e.getMessage(), e);
        }
    }

    @Nonnull
    private DynamicApplicationProperties readApplicationPropertiesFrom(Path applicationPropertiesPath) {
        try {
            String dynamicApplicationPropertiesJson = Files.readString(applicationPropertiesPath);
            log.info("Retrieved dynamicApplicationProperties: {}", dynamicApplicationPropertiesJson);

            return objectMapper.readValue(dynamicApplicationPropertiesJson, DynamicApplicationProperties.class);
        } catch (IOException e) {
            log.info("Unable to retrieve dynamicApplicationProperties: {}", e.getMessage(), e);
        }
        return new DynamicApplicationProperties();
    }

    private Port createPort(ApplicationModule applicationModule,
                            int managementPortNumber) {
        Port managementPort = new Port();
        managementPort.setPort(managementPortNumber);
        managementPort.setModifiedBy(applicationModule);
        managementPort.setModifiedAt(Instant.now());
        return managementPort;
    }


}
