package com.cgi.example.petstore.integration;

import com.cgi.example.petstore.model.Customer;
import com.cgi.example.petstore.model.NewPet;
import com.cgi.example.petstore.model.PetInformationItem;
import com.cgi.example.petstore.model.PetPatch;
import com.cgi.example.petstore.model.PetStatus;
import com.cgi.example.petstore.service.persistence.pet.PetDocument;
import com.cgi.example.petstore.service.persistence.pet.PetRepository;
import com.cgi.example.petstore.utils.TestData;
import com.jayway.jsonpath.JsonPath;
import jakarta.validation.Valid;
import org.hamcrest.CoreMatchers;
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

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
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

        URI uri = uriBuilder.getPetStoreBaseURI()
                .build()
                .toUri();
        RequestEntity<NewPet> requestEntity = new RequestEntity<>(petToAdd, HttpMethod.POST, uri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = """
                      {
                        "petId": 10,
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
        PetDocument actualPetDocument = actualAllPetDocuments.getFirst();
        assertEquals(10L, actualPetDocument.getPetId());
    }

    @Test
    void shouldReturnFidoWhenCallingGetPetEndpoint() {
        PetDocument petDocument = testData.createPetDocument();
        Long petId = petDocument.getPetId();
        petRepository.save(petDocument);

        assertThat("Failed precondition", petRepository.findAll(), Matchers.iterableWithSize(1));

        URI uri = uriBuilder.getPetStoreURIFor(String.valueOf(petId))
                .build()
                .toUri();
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET,
                uri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = """
                      {
                        "petId": 10,
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
        assertThat("Failed precondition", petRepository.findAll(), Matchers.empty());

        URI uri = uriBuilder.getPetStoreURIFor("13")
                .build()
                .toUri();

        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET, uri);

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
        assertThat("Failed precondition", petRepository.findAll(), Matchers.empty());

        URI uri = uriBuilder.getPetStoreURIFor("10000")
                .build()
                .toUri();

        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET, uri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        int status = JsonPath.read(response.getBody(), "$.status");
        String instance = JsonPath.read(response.getBody(), "$.instance");
        String detail = JsonPath.read(response.getBody(), "$.detail");

        assertAll(
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode()),
                assertions.assertContentType(response, MediaType.APPLICATION_PROBLEM_JSON_VALUE),
                () -> assertEquals(500, status),
                () -> assertEquals("Handled by GlobalExceptionHandler - [getPetById.petId: must be less than or equal to 2000]", detail),
                () -> assertThat(instance, CoreMatchers.containsString(uriBuilder.PET_STORE_BASE_URL + "/10000")));
    }

    @Test
    void shouldReturnErrorWhenCallingGetPetEndpointWithInvalidIdFailingValidation() {
        assertThat("Failed precondition", petRepository.findAll(), Matchers.empty());

        URI uri = uriBuilder.getPetStoreURIFor("666")
                .build()
                .toUri();
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET, uri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        int status = JsonPath.read(response.getBody(), "$.status");
        String instance = JsonPath.read(response.getBody(), "$.instance");
        String detail = JsonPath.read(response.getBody(), "$.detail");

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode()),
                assertions.assertContentType(response, MediaType.APPLICATION_PROBLEM_JSON_VALUE),
                () -> assertEquals(HttpStatus.BAD_REQUEST.value(), status),
                () -> assertEquals("Handled by GlobalExceptionHandler - [Invalid Pet Id, the Id 666 is not permitted, found: 666]", detail),
                () -> assertThat(instance, CoreMatchers.containsString(uriBuilder.PET_STORE_BASE_URL + "/666"))
        );
    }

    @Test
    void shouldReturnPetsWithMatchingStatusesWhenCallingFindByStatus() {
        PetDocument petDocumentLassie = createPetDocument(10L,
                "Lassie",
                PetStatus.PENDING_COLLECTION);
        PetDocument petDocumentAstro = createPetDocument(11L,
                "Astro",
                PetStatus.SOLD);
        PetDocument petDocumentBeethoven = createPetDocument(12L,
                "Beethoven",
                PetStatus.AVAILABLE_FOR_PURCHASE);

        petRepository.saveAll(Arrays.asList(petDocumentLassie,
                petDocumentAstro,
                petDocumentBeethoven));

        assertThat("Failed precondition", petRepository.findAll(), Matchers.iterableWithSize(3));

        URI uri = uriBuilder.getPetStoreBaseURI()
                .pathSegment("findByStatus")
                .queryParam("statuses", PetStatus.AVAILABLE_FOR_PURCHASE.name())
                .build()
                .toUri();

        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET, uri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = """
                    {
                      "pets": [
                        {
                          "petStatus": "Available For Purchase",
                          "petId": 12,
                          "vaccinationId": "AF54785412K",
                          "name": "Beethoven",
                          "petType": "Dog",
                          "photoUrls": [
                            "https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6"
                          ]
                        }
                      ]
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
    void shouldUpdateExistingPetWithNewNameAndInformationWhenPatchEndpointIsCalled() {
        PetDocument petDocumentBeethoven = createPetDocument(12L,
                "Beethoven",
                PetStatus.AVAILABLE_FOR_PURCHASE);

        petRepository.save(petDocumentBeethoven);

        assertThat("Failed precondition", petRepository.findAll(), Matchers.iterableWithSize(1));

        URI uri = uriBuilder.getPetStoreBaseURI()
                .build()
                .toUri();

        PetPatch petPatch = new PetPatch();
        petPatch.setId(12L);
        petPatch.setName("Astro");
        List<@Valid PetInformationItem> additionalInformation =
                Collections.singletonList(testData.createPetInformationItem("Eye colour", "Green"));
        petPatch.setAdditionalInformation(additionalInformation);

        RequestEntity<PetPatch> requestEntity = new RequestEntity<>(petPatch, HttpMethod.PATCH, uri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = """
                    {
                      "petStatus": "Available For Purchase",
                      "petId": 12,
                      "vaccinationId": "AF54785412K",
                      "name": "Astro",
                      "petType": "Dog",
                      "photoUrls": [
                        "https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6"
                      ],
                      "additionalInformation": [
                        {
                          "name": "Eye colour",
                          "description": "Green"
                        }
                      ]
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
    void shouldSuccessfullyPurchaseAPet() {
        PetDocument savedPetDocument = petRepository.save(testData.createPetDocument());

        assertThat("Failed precondition", petRepository.findAll(), Matchers.iterableWithSize(1));

        URI uri = uriBuilder.getPetStoreURIFor(String.valueOf(savedPetDocument.getPetId()))
                .build()
                .toUri();
        RequestEntity<Customer> requestEntity = new RequestEntity<>(testData.createCustomer(),
                HttpMethod.POST,
                uri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = """
                    {
                      "petStatus": "Pending Collection",
                      "owner": {
                        "customerId": 246879,
                        "username": "alex.stone",
                        "firstName": "Alex",
                        "lastName": "Stone",
                        "email": "alex.stone@cgi.com",
                        "address": {
                          "street": "40 Princes Street",
                          "city": "Edinburgh",
                          "postCode": "EH2 2BY",
                          "country": "United Kingdom"
                        }
                      },
                      "petId": 10,
                      "vaccinationId": "AF54785412K",
                      "name": "Fido",
                      "petType": "Dog",
                      "photoUrls": [
                        "https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6"
                      ]
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
        assertEquals(10L, allPetDocument.getPetId());
    }

    private PetDocument createPetDocument(long id,
                                          String name,
                                          PetStatus petStatus) {
        PetDocument petDocumentBeethoven = testData.createPetDocument();

        petDocumentBeethoven.setPetId(id);
        petDocumentBeethoven.setName(name);
        petDocumentBeethoven.setPetStatus(petStatus.getValue());

        return petDocumentBeethoven;
    }
}
