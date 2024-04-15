package com.cgi.example.common.local;

import com.cgi.example.common.DynamicApplicationFileProperties;
import com.cgi.example.common.local.model.ApplicationModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DynamicApplicationPropertiesRepositoryTest {

    private static final ApplicationModule APPLICATION_MODULE = ApplicationModule.APPLICATION_MAIN;

    private DynamicApplicationPropertiesRepository repository;

    @BeforeEach
    void setUp() {
        deleteDynamicPropertiesFile();
        repository = new DynamicApplicationPropertiesRepository();
    }

    @AfterEach
    void tearDown() {
        deleteDynamicPropertiesFile();
    }

    private void deleteDynamicPropertiesFile() {
        File dynamicPropertiesFile = Path.of(DynamicApplicationFileProperties.FILE_PATH).toFile();
        dynamicPropertiesFile.delete();
    }

    @Test
    void shouldReturnNullIfApplicationPortIsNotDefined() {
        assertNull(repository.getApplicationPort());
    }

    @Test
    void shouldReturnNullIfManagementPortIsNotDefined() {
        assertNull(repository.getManagementPort());
    }

    @Test
    void shouldReturnNullIfWireMockPortIsNotDefined() {
        assertNull(repository.getWireMockPort());
    }

    @Test
    void setApplicationPortShouldSuccessfullyPersist() {
        int applicationPort = 9099;
        repository.setApplicationPort(APPLICATION_MODULE, applicationPort);

        Integer actualApplicationPort = repository.getApplicationPort();

        assertNotNull(actualApplicationPort);
        assertEquals(applicationPort, actualApplicationPort);
    }

    @Test
    void setManagementPortShouldSuccessfullyPersist() {
        int managementPort = 8099;
        repository.setManagementPort(APPLICATION_MODULE, managementPort);

        Integer actualManagementPort = repository.getManagementPort();

        assertNotNull(actualManagementPort);
        assertEquals(managementPort, actualManagementPort);
    }

    @Test
    void setWireMockPortShouldSuccessfullyPersist() {
        int wireMockPort = 8000;
        repository.setWireMockPort(APPLICATION_MODULE, wireMockPort);

        Integer actualWireMockPort = repository.getWireMockPort();

        assertNotNull(actualWireMockPort);
        assertEquals(wireMockPort, actualWireMockPort);
    }

    @Test
    void setMongoDBPortShouldSuccessfullyPersist() {
        int mongoDBPort = 8000;
        repository.setMongoDBPort(APPLICATION_MODULE, mongoDBPort);

        String connectionString = repository.getMongoDBConnectionString();

        assertNotNull(connectionString);
        assertEquals("mongodb://localhost:8000", connectionString);
    }

    @Test
    void shouldBeAbleToSetAndGetAllPortNumbersIndependently() {
        int applicationPort = 9099;
        repository.setApplicationPort(APPLICATION_MODULE, applicationPort);

        int managementPort = 8099;
        repository.setManagementPort(APPLICATION_MODULE, managementPort);

        int wireMockPort = 8000;
        repository.setWireMockPort(APPLICATION_MODULE, wireMockPort);

        int mongoDBPort = 7044;
        repository.setMongoDBPort(APPLICATION_MODULE, mongoDBPort);

        assertEquals(3, Set.of(applicationPort, managementPort, wireMockPort).size(),
                "Failed precondition, expected all port numbers to be unique");

        assertEquals(applicationPort, repository.getApplicationPort());
        assertEquals(managementPort, repository.getManagementPort());
        assertEquals(wireMockPort, repository.getWireMockPort());
        assertEquals("mongodb://localhost:7044", repository.getMongoDBConnectionString());
    }
}