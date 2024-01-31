package com.cgi.example.petstore.integration;

import com.cgi.example.petstore.integration.utils.BaseIntegrationTest;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ActuatorAndDocsIntegrationTest extends BaseIntegrationTest {

    @Test
    public void actuatorEndpointShouldListResources() {
        RequestEntity<String> requestEntity = requestFactory.createManagementRequest(HttpMethod.GET, "/actuator");

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        Set<String> links = JsonPath.read(response.getBody(), "$._links.keys()");

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                assertions.assertJSONContentType(response),
                () -> assertThat(links, Matchers.containsInAnyOrder("self", "health", "health-path",
                        "info", "metrics-requiredMetricName", "metrics", "mappings"))
        );
    }

    @Test
    void actuatorHealthEndpointShouldShowUp() {
        RequestEntity<String> requestEntity = requestFactory.createManagementRequest(HttpMethod.GET, "/actuator/health");

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String status = JsonPath.read(response.getBody(), "$.status");
        String pingStatus = JsonPath.read(response.getBody(), "$.components.ping.status");

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                assertions.assertJSONContentType(response),
                () -> assertEquals("UP", status),
                () -> assertEquals("UP", pingStatus)
        );
    }

    @Test
    void actuatorInfoEndpointShouldIncludeDescriptionArtifactNameAndGroup() {
        RequestEntity<String> requestEntity = requestFactory.createManagementRequest(HttpMethod.GET, "/actuator/info");

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String description = JsonPath.read(response.getBody(), "$.build.description");
        String artifact = JsonPath.read(response.getBody(), "$.build.artifact");
        String name = JsonPath.read(response.getBody(), "$.build.name");
        String group = JsonPath.read(response.getBody(), "$.build.group");

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                assertions.assertJSONContentType(response),
                () -> assertEquals("Spring Boot Template Service modeled on an online Pet Store", description),
                () -> assertEquals("spring-boot-microservice-template", artifact),
                () -> assertEquals("spring-boot-microservice-template", name),
                () -> assertEquals("com.cgi.example", group)
        );
    }

    @Test
    void actuatorMappingsEndpointShouldListMultipleMappings() {
        RequestEntity<String> requestEntity = requestFactory.createManagementRequest(HttpMethod.GET, "/actuator/mappings");

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        int numberOfMappings = JsonPath.read(response.getBody(), "$.contexts.application.mappings.dispatcherServlets.dispatcherServlet.length()");

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                assertions.assertJSONContentType(response),
                () -> assertThat(numberOfMappings, Matchers.greaterThan(3))
        );
    }

    @ParameterizedTest
    @CsvSource({"/v3/api-docs,application/json",
            "/v3/api-docs.yaml,application/vnd.oai.openapi",
            "/v3/api-docs/springdoc,application/json"
    })
    void shouldReturnApiDefinitionsWhenCallingApiDocsEndpoints(String apiDocUrl, String expectedContentType) {
        RequestEntity<String> requestEntity = requestFactory.createApplicationRequest(HttpMethod.GET, apiDocUrl);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                assertions.assertContentType(response, expectedContentType),
                () -> assertThat(response.getBody(), Matchers.containsString(PET_STORE_BASE_URL + "{petId}")),
                () -> assertThat(response.getBody(), Matchers.containsString("Find pet by ID"))
        );
    }

    @Test
    void shouldReturnApiDefinitionsWhenCallingApiDocsEndpoints() {
        RequestEntity<String> requestEntity = requestFactory.createApplicationRequest(HttpMethod.GET, "/v3/api-docs/swagger-config");

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        int numberOfURLs = JsonPath.read(response.getBody(), "$.urls.length()");
        String url = JsonPath.read(response.getBody(), "$.urls[0].url");

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                assertions.assertJSONContentType(response),
                () -> assertEquals(1, numberOfURLs),
                () -> assertEquals("/v3/api-docs/springdoc", url)
        );
    }
}
