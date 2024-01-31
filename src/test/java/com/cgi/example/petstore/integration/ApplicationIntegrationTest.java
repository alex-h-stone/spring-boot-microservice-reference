package com.cgi.example.petstore.integration;

import com.cgi.example.petstore.integration.utils.BaseIntegrationTest;
import com.cgi.example.petstore.service.persistence.PetRepository;
import com.cgi.example.petstore.utils.TestData;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationIntegrationTest extends BaseIntegrationTest {

    private final TestData testData = new TestData();

    @Autowired
    private PetRepository petRepository;

    @Test
    void shouldReturnFidoWhenCallingGetPetEndpoint() {
        petRepository.save(testData.createPetDocument());
        RequestEntity<String> requestEntity = requestFactory.createApplicationRequest(HttpMethod.GET, PET_STORE_BASE_URL + "10");

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = """
                      {
                        "id": 10,
                        "vaccinationId": "AF54785412K",
                        "name": "Fido",
                        "petType": "Dog",
                        "petStatus": "Available For Purchase"
                      }
                """;

        String actualJsonBody = response.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                assertions.assertJSONContentType(response),
                () -> JSONAssert.assertEquals(expectedJsonBody, actualJsonBody, JSONCompareMode.LENIENT)
        );
    }

    @Test
    void shouldReturnErrorWhenCallingGetPetEndpointWithIdLargerThanPermitted() {
        RequestEntity<String> requestEntity = requestFactory.createApplicationRequest(HttpMethod.GET, PET_STORE_BASE_URL + "10000");

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        int status = JsonPath.read(response.getBody(), "$.status");
        String instance = JsonPath.read(response.getBody(), "$.instance");
        String detail = JsonPath.read(response.getBody(), "$.detail");

        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                assertions.assertContentType(response, MediaType.APPLICATION_PROBLEM_JSON_VALUE),
                () -> assertEquals(500, status),
                () -> assertEquals("Handled by class com.cgi.example.petstore.handler.GlobalExceptionHandler - [getPetById.petId: must be less than or equal to 2000]", detail),
                () -> assertEquals(PET_STORE_BASE_URL + "10000", instance));
    }

    @Test
    void shouldReturnErrorWhenCallingGetPetEndpointWithInvalidIdFailingValidation() {
        RequestEntity<String> requestEntity = requestFactory.createApplicationRequest(HttpMethod.GET, PET_STORE_BASE_URL + "666");

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        int status = JsonPath.read(response.getBody(), "$.status");
        String instance = JsonPath.read(response.getBody(), "$.instance");
        String detail = JsonPath.read(response.getBody(), "$.detail");

        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                assertions.assertContentType(response, MediaType.APPLICATION_PROBLEM_JSON_VALUE),
                () -> assertEquals(500, status),
                () -> assertEquals("Handled by class com.cgi.example.petstore.handler.GlobalExceptionHandler - [Invalid Pet ID: 666]", detail),
                () -> assertEquals(PET_STORE_BASE_URL + "666", instance)
        );
    }
}
