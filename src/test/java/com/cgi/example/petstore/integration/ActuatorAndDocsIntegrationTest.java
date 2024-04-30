package com.cgi.example.petstore.integration;

import com.cgi.example.petstore.integration.utils.UriBuilder;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
class ActuatorAndDocsIntegrationTest extends BaseIntegrationTest {

    @Test
    public void actuatorEndpointShouldListResources() {
        UriComponentsBuilder uri = uriBuilder.getManagementURIFor("actuator");
        ResponseEntity<String> response = webClientExecutor.get(uri);

        Set<String> links = JsonPath.read(response.getBody(), "$._links.keys()");

        assertAll(
                assertions.assertStatusCode(response, HttpStatus.OK),
                assertions.assertContentType(response, "application/vnd.spring-boot.actuator.v3+json"),
                () ->
                        assertThat(
                                links,
                                Matchers.containsInAnyOrder(
                                        "self",
                                        "beans",
                                        "health",
                                        "health-path",
                                        "info",
                                        "configprops",
                                        "configprops-prefix",
                                        "env",
                                        "env-toMatch",
                                        "loggers",
                                        "loggers-name",
                                        "metrics-requiredMetricName",
                                        "metrics",
                                        "mappings")));
    }

    @Test
    void actuatorHealthEndpointShouldShowUp() {
        UriComponentsBuilder uri = uriBuilder.getManagementURIFor("actuator/health");
        ResponseEntity<String> response = webClientExecutor.get(uri);

        String status = JsonPath.read(response.getBody(), "$.status");
        String pingStatus = JsonPath.read(response.getBody(), "$.components.ping.status");

        assertAll(
                assertions.assertStatusCode(response, HttpStatus.OK),
                assertions.assertContentType(response, "application/vnd.spring-boot.actuator.v3+json"),
                () -> assertEquals("UP", status),
                () -> assertEquals("UP", pingStatus));
    }

    @Test
    void actuatorInfoEndpointShouldIncludeDescriptionArtifactNameAndGroup() {
        UriComponentsBuilder uri = uriBuilder.getManagementURIFor("actuator/info");

        ResponseEntity<String> response = webClientExecutor.get(uri);

        String description = JsonPath.read(response.getBody(), "$.build.description");
        String artifact = JsonPath.read(response.getBody(), "$.build.artifact");
        String name = JsonPath.read(response.getBody(), "$.build.name");
        String group = JsonPath.read(response.getBody(), "$.build.group");

        assertAll(
                assertions.assertStatusCode(response, HttpStatus.OK),
                assertions.assertContentType(response, "application/vnd.spring-boot.actuator.v3+json"),
                () ->
                        assertEquals(
                                "Spring Boot Template Service modeled on an online Pet Store.", description),
                () -> assertEquals("spring-boot-microservice-template", artifact),
                () -> assertEquals("spring-boot-microservice-template", name),
                () -> assertEquals("com.cgi.example", group));
    }

    @ParameterizedTest
    @CsvSource({
            "v3/api-docs,application/json",
            "v3/api-docs.yaml,application/vnd.oai.openapi",
            "v3/api-docs/springdoc,application/json",
    })
    void should_ReturnApiDefinitionWhenCallingApiDocsEndpoints(
            String apiDocUrl, String expectedContentType) {
        UriComponentsBuilder uri = uriBuilder.getApplicationURIFor(apiDocUrl);
        ResponseEntity<String> response = webClientExecutor.get(uri);

        String responseBody = response.getBody();
        assertAll(
                assertions.assertStatusCode(response, HttpStatus.OK),
                assertions.assertContentType(response, expectedContentType),
                assertions.assertContains(responseBody, UriBuilder.PET_STORE_BASE_URL + "/{petId}"),
                assertions.assertContains(responseBody, "Find pet by Id"),
                assertions.assertContains(responseBody, "Operations on the Pet Store concerning pets."));
    }

    @Test
    void actuatorMappingsEndpointShouldListMultipleMappings() {
        UriComponentsBuilder uri = uriBuilder.getManagementURIFor("actuator/mappings");

        ResponseEntity<String> response = webClientExecutor.get(uri);

        int numberOfMappings =
                JsonPath.read(
                        response.getBody(),
                        "$.contexts.application.mappings.dispatcherServlets.dispatcherServlet.length()");

        assertAll(
                assertions.assertStatusCode(response, HttpStatus.OK),
                assertions.assertContentType(response, "application/vnd.spring-boot.actuator.v3+json"),
                () -> assertThat(numberOfMappings, Matchers.greaterThan(3)));
    }

    @Test
    void should_ReturnApiDefinitionWhenCallingApiDocsEndpoint() {
        UriComponentsBuilder uri = uriBuilder.getApplicationURIFor("v3/api-docs/swagger-config");
        ResponseEntity<String> response = webClientExecutor.get(uri);

        int numberOfURLs = JsonPath.read(response.getBody(), "$.urls.length()");
        String url = JsonPath.read(response.getBody(), "$.urls[0].url");

        assertAll(
                assertions.assertOkJsonResponse(response),
                () -> assertEquals(1, numberOfURLs),
                () -> assertEquals("/v3/api-docs/springdoc", url));
    }

    @Test
    void should_ReturnApiDefinition_When_CallingSwaggerUiIndexHtmlEndpoint() {
        UriComponentsBuilder uri = uriBuilder.getApplicationURIFor("swagger-ui/index.html");
        ResponseEntity<String> response = webClientExecutor.get(uri);

        String responseBody = response.getBody();

        assertAll(
                assertions.assertStatusCode(response, HttpStatus.OK),
                assertions.assertContentType(response, "text/html"),
                assertions.assertContains(responseBody, "swagger-ui"),
                assertions.assertContains(responseBody, "html"));
    }
}
