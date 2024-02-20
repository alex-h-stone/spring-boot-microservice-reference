package com.cgi.example.petstore.integration;

import com.cgi.example.petstore.integration.utils.UriBuilder;
import com.cgi.example.petstore.model.CustomerRequest;
import com.cgi.example.petstore.model.NewPet;
import com.cgi.example.petstore.model.PetInformationItem;
import com.cgi.example.petstore.model.PetPatch;
import com.cgi.example.petstore.model.PetStatus;
import com.cgi.example.petstore.service.pet.PetDocument;
import com.cgi.example.petstore.service.pet.PetRepository;
import com.cgi.example.petstore.utils.TestData;
import com.jayway.jsonpath.JsonPath;
import jakarta.validation.Valid;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
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
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
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
                        "vaccinationId": "AF54785412K",
                        "name": "Fido",
                        "petType": "Dog",
                        "petStatus": "Available For Purchase"
                      }
                """;

        String actualJsonBody = response.getBody();
        String actualGeneratedPetId = JsonPath.read(actualJsonBody, "$.petId");

        assertions.assertOkJSONResponse(response);
        assertions.assertLenientJSONEquals(expectedJsonBody, actualJsonBody);
        assertThat(actualGeneratedPetId, not(isEmptyOrNullString()));

        List<PetDocument> actualAllPetDocuments = petRepository.findAll();
        assertThat(actualAllPetDocuments, Matchers.iterableWithSize(1));
        PetDocument actualPetDocument = actualAllPetDocuments.getFirst();
        assertEquals(actualGeneratedPetId, actualPetDocument.getPetId());
    }

    @Test
    void shouldReturnFidoWhenCallingGetPetEndpoint() {
        PetDocument petDocument = testData.createPetDocument();
        String petId = petDocument.getPetId();
        petRepository.save(petDocument);

        assertThat("Failed precondition", petRepository.findAll(), Matchers.iterableWithSize(1));

        URI uri = uriBuilder.getPetStoreURIFor(petId)
                .build()
                .toUri();
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET,
                uri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = """
                      {
                        "petId": "KT1546",
                        "vaccinationId": "AF54785412K",
                        "name": "Fido",
                        "petType": "Dog",
                        "petStatus": "Available For Purchase"
                      }
                """;

        String actualJsonBody = response.getBody();

        assertions.assertOkJSONResponse(response);
        assertions.assertLenientJSONEquals(expectedJsonBody, actualJsonBody);
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
                       "detail": "Handled by GlobalExceptionHandler: [Unable to find the pet with Id: [13]]",
                       "instance": "/api/v1/pet-store/pets/13"
                     }
                """;

        String actualJsonBody = response.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()),
                assertions.assertContentType(response, MediaType.APPLICATION_PROBLEM_JSON_VALUE),
                () -> assertions.assertLenientJSONEquals(expectedJsonBody, actualJsonBody)
        );
    }

    @Test
    void shouldReturnErrorWhenCallingGetPetEndpointWithIdLargerThanPermitted() {
        assertThat("Failed precondition", petRepository.findAll(), Matchers.empty());

        URI uri = uriBuilder.getPetStoreURIFor("abcdefghijklmnopqrstuvwxyz0123456789")
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
                () -> assertEquals("Handled by GlobalExceptionHandler: [getPetById.petId: size must be between 0 and 26]", detail),
                () -> assertThat(instance, CoreMatchers.containsString(UriBuilder.PET_STORE_BASE_URL + "/abcdefghijklmnopqrstuvwxyz0123456789")));
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
                () -> assertEquals("Handled by GlobalExceptionHandler: [Invalid Pet Id, the Id [666] is not permitted, found: [666]]", detail),
                () -> assertThat(instance, CoreMatchers.containsString(UriBuilder.PET_STORE_BASE_URL + "/666"))
        );
    }

    @Test
    void shouldReturnPetsWithMatchingStatusesWhenCallingFindByStatus() {
        PetDocument petDocumentLassie = createPetDocument(
                "KT1546", "Lassie",
                PetStatus.PENDING_COLLECTION);
        PetDocument petDocumentAstro = createPetDocument(
                "ABC456", "Astro",
                PetStatus.SOLD);
        PetDocument petDocumentBeethoven = createPetDocument(
                "XYZ987", "Beethoven",
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
                          "petId": "XYZ987",
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

        assertions.assertOkJSONResponse(response);
        assertions.assertLenientJSONEquals(expectedJsonBody, actualJsonBody);
    }

    @Test
    void shouldUpdateExistingPetWithNewNameAndInformationWhenPatchEndpointIsCalled() {
        PetDocument petDocumentBeethoven = createPetDocument(
                "XYZ987", "Beethoven",
                PetStatus.AVAILABLE_FOR_PURCHASE);

        petRepository.save(petDocumentBeethoven);

        assertThat("Failed precondition", petRepository.findAll(), Matchers.iterableWithSize(1));

        URI uri = uriBuilder.getPetStoreBaseURI()
                .build()
                .toUri();

        PetPatch petPatch = new PetPatch();
        petPatch.setId("XYZ987");
        petPatch.setName("Astro");
        List<@Valid PetInformationItem> additionalInformation =
                Collections.singletonList(testData.createPetInformationItem("Eye colour", "Green"));
        petPatch.setAdditionalInformation(additionalInformation);

        RequestEntity<PetPatch> requestEntity = new RequestEntity<>(petPatch, HttpMethod.PATCH, uri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = """
                    {
                      "petStatus": "Available For Purchase",
                      "petId": "XYZ987",
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

        assertions.assertOkJSONResponse(response);
        assertions.assertLenientJSONEquals(expectedJsonBody, actualJsonBody);
    }

    @Test
    void shouldSuccessfullyPurchaseAPet() {
        PetDocument savedPetDocument = petRepository.save(testData.createPetDocument());

        assertThat("Failed precondition", petRepository.findAll(), Matchers.iterableWithSize(1));

        URI uri = uriBuilder.getPetStoreURIFor(savedPetDocument.getPetId())
                .build()
                .toUri();
        RequestEntity<CustomerRequest> requestEntity = new RequestEntity<>(testData.createCustomer(),
                HttpMethod.POST,
                uri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = """
                    {
                      "petStatus": "Pending Collection",
                      "owner": {
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
                      "petId": "KT1546",
                      "vaccinationId": "AF54785412K",
                      "name": "Fido",
                      "petType": "Dog",
                      "photoUrls": [
                        "https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6"
                      ]
                    }
                """;

        String actualJsonBody = response.getBody();
        String actualCustomerId = JsonPath.read(response.getBody(), "$.owner.customerId");

        assertions.assertOkJSONResponse(response);
        assertions.assertLenientJSONEquals(expectedJsonBody, actualJsonBody);
        assertThat(actualCustomerId, not(isEmptyOrNullString()));

        List<PetDocument> actualAllPetDocuments = petRepository.findAll();
        assertThat(actualAllPetDocuments, Matchers.iterableWithSize(1));
        PetDocument allPetDocument = actualAllPetDocuments.getFirst();
        assertEquals("KT1546", allPetDocument.getPetId());
    }

    private PetDocument createPetDocument(String petId,
                                          String name,
                                          PetStatus petStatus) {
        PetDocument petDocument = testData.createPetDocument(petId);

        petDocument.setPetId(petId);
        petDocument.setName(name);
        petDocument.setPetStatus(petStatus.getValue());

        return petDocument;
    }
}
