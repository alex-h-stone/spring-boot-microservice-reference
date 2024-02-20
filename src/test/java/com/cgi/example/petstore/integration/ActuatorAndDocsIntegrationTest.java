package com.cgi.example.petstore.integration;

import com.cgi.example.petstore.integration.utils.UriBuilder;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ActuatorAndDocsIntegrationTest extends BaseIntegrationTest {

    @Test
    public void actuatorEndpointShouldListResources() {
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET,
                uriBuilder.getManagementURIFor("actuator").build().toUri());

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        Set<String> links = JsonPath.read(response.getBody(), "$._links.keys()");

        assertAll(
                assertions.assertOkJsonResponse(response),
                () -> assertThat(links, Matchers.containsInAnyOrder("self", "health", "health-path",
                        "info", "metrics-requiredMetricName", "metrics", "mappings"))
        );
    }

    @Test
    void actuatorHealthEndpointShouldShowUp() {
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET,
                uriBuilder.getManagementURIFor("actuator/health").build().toUri());

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String status = JsonPath.read(response.getBody(), "$.status");
        String pingStatus = JsonPath.read(response.getBody(), "$.components.ping.status");

        assertAll(
                assertions.assertOkJsonResponse(response),
                () -> assertEquals("UP", status),
                () -> assertEquals("UP", pingStatus)
        );
    }

    @Test
    void actuatorInfoEndpointShouldIncludeDescriptionArtifactNameAndGroup() {
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET,
                uriBuilder.getManagementURIFor("actuator/info").build().toUri());

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String description = JsonPath.read(response.getBody(), "$.build.description");
        String artifact = JsonPath.read(response.getBody(), "$.build.artifact");
        String name = JsonPath.read(response.getBody(), "$.build.name");
        String group = JsonPath.read(response.getBody(), "$.build.group");

        assertAll(
                assertions.assertOkJsonResponse(response),
                () -> assertEquals("Spring Boot Template Service modeled on an online Pet Store.", description),
                () -> assertEquals("spring-boot-microservice-template", artifact),
                () -> assertEquals("spring-boot-microservice-template", name),
                () -> assertEquals("com.cgi.example", group)
        );
    }

    @Test
    void actuatorMappingsEndpointShouldListMultipleMappings() {
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET,
                uriBuilder.getManagementURIFor("actuator/mappings").build().toUri());

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        int numberOfMappings = JsonPath.read(response.getBody(), "$.contexts.application.mappings.dispatcherServlets.dispatcherServlet.length()");

        assertAll(
                assertions.assertOkJsonResponse(response),
                () -> assertThat(numberOfMappings, Matchers.greaterThan(3))
        );
    }

    @ParameterizedTest
    @CsvSource({"v3/api-docs,application/json",
            "v3/api-docs.yaml,application/vnd.oai.openapi",
            "v3/api-docs/springdoc,application/json"
    })
    void shouldReturnApiDefinitionsWhenCallingApiDocsEndpoints(String apiDocUrl, String expectedContentType) {
        URI uri = uriBuilder.getApplicationURIFor(apiDocUrl).build().toUri();

        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET, uri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertThat(response.getBody(), Matchers.containsString(UriBuilder.PET_STORE_BASE_URL + "/{petId}")),
                () -> assertThat(response.getBody(), Matchers.containsString("Find pet by Id"))
        );
    }

    @Test
    void shouldReturnApiDefinitionsWhenCallingApiDocsEndpoints() {
        URI requestUri = uriBuilder.getApplicationURIFor("v3/api-docs/swagger-config").build().toUri();
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET, requestUri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        int numberOfURLs = JsonPath.read(response.getBody(), "$.urls.length()");
        String url = JsonPath.read(response.getBody(), "$.urls[0].url");

        assertAll(
                assertions.assertOkJsonResponse(response),
                () -> assertEquals(1, numberOfURLs),
                () -> assertEquals("/v3/api-docs/springdoc", url)
        );
    }
}
