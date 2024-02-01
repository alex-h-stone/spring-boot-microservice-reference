package com.cgi.example.petstore.integration;

import com.cgi.example.petstore.model.NewPet;
import com.cgi.example.petstore.service.persistence.PetDocument;
import com.cgi.example.petstore.service.persistence.PetRepository;
import com.cgi.example.petstore.utils.TestData;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationIntegrationTest extends BaseIntegrationTest {

    private final TestData testData = new TestData();

    @Autowired
    private PetRepository petRepository;

    @Test
    void shouldSuccessfullyAddPet() {
        NewPet petToAdd = testData.createNewPet();

        assertThat("Failed precondition", petRepository.findAll(), Matchers.empty());

        RequestEntity<NewPet> requestEntity = new RequestEntity<>(petToAdd,
                HttpMethod.POST,
                requestURI.getApplicationURIFor(PET_STORE_BASE_URL));

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

        List<PetDocument> actualAllPetDocuments = petRepository.findAll();
        assertThat(actualAllPetDocuments, Matchers.iterableWithSize(1));
        PetDocument allPetDocument = actualAllPetDocuments.getFirst();
        assertEquals("gfdg", allPetDocument.getId());
    }

    @Test
    void shouldReturnFidoWhenCallingGetPetEndpoint() {
        PetDocument petDocument = testData.createPetDocument();
        String petId = petDocument.getId();
        petRepository.save(petDocument);
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET,
                requestURI.getApplicationURIFor(PET_STORE_BASE_URL + petId));

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
    void shouldReturnNotFoundWhenCallingGetPetWithUnknownPetId() {
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET,
                requestURI.getApplicationURIFor(PET_STORE_BASE_URL + 13));

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = """
                     {
                       "type": "about:blank",
                       "title": "Not Found",
                       "status": 404,
                       "detail": "Handled by GlobalExceptionHandler - [Unable to find the pet with Id: [13]]",
                       "instance": "/api/v1/pet-store/pets/13"
                     }
                """;

        String actualJsonBody = response.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()),
                assertions.assertContentType(response, MediaType.APPLICATION_PROBLEM_JSON_VALUE),
                () -> JSONAssert.assertEquals(expectedJsonBody, actualJsonBody, JSONCompareMode.LENIENT)
        );
    }

    @Test
    void shouldReturnErrorWhenCallingGetPetEndpointWithIdLargerThanPermitted() {
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET,
                requestURI.getApplicationURIFor(PET_STORE_BASE_URL + "10000"));

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        int status = JsonPath.read(response.getBody(), "$.status");
        String instance = JsonPath.read(response.getBody(), "$.instance");
        String detail = JsonPath.read(response.getBody(), "$.detail");

        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                assertions.assertContentType(response, MediaType.APPLICATION_PROBLEM_JSON_VALUE),
                () -> assertEquals(500, status),
                () -> assertEquals("Handled by GlobalExceptionHandler - [getPetById.petId: must be less than or equal to 2000]", detail),
                () -> assertEquals(PET_STORE_BASE_URL + "10000", instance));
    }

    @Test
    void shouldReturnErrorWhenCallingGetPetEndpointWithInvalidIdFailingValidation() {
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET,
                requestURI.getApplicationURIFor(PET_STORE_BASE_URL + "666"));

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        int status = JsonPath.read(response.getBody(), "$.status");
        String instance = JsonPath.read(response.getBody(), "$.instance");
        String detail = JsonPath.read(response.getBody(), "$.detail");

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                assertions.assertContentType(response, MediaType.APPLICATION_PROBLEM_JSON_VALUE),
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), status),
                () -> assertEquals("Handled by GlobalExceptionHandler - [Invalid Pet ID: 666]", detail),
                () -> assertEquals(PET_STORE_BASE_URL + "666", instance)
        );
    }
}
