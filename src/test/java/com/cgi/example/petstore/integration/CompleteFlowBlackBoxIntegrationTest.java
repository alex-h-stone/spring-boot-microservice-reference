package com.cgi.example.petstore.integration;

import com.cgi.example.petstore.model.CustomerRequest;
import com.cgi.example.petstore.model.NewPetRequest;
import com.cgi.example.petstore.model.PetInformationItem;
import com.cgi.example.petstore.model.PetPatchRequest;
import com.cgi.example.petstore.model.PetStatus;
import com.cgi.example.petstore.utils.TestData;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.jayway.jsonpath.JsonPath;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompleteFlowBlackBoxIntegrationTest extends BaseIntegrationTest {

    private static final List<String> ALL_PET_STATUSES = Arrays.stream(PetStatus.values())
            .map(Enum::name)
            .collect(Collectors.toList());

    private final TestData testData = new TestData();

    @Test
    void shouldSuccessfullyAddModifyFindUpdateAndPurchaseAPet() {
        String vaccinations = fileUtils.readFile("external\\animalvaccinationapi\\response\\vaccinationResponseMultiple.json");
        stubServer.stubFor(WireMock.get(urlEqualTo("/vaccinations/AF54785412K"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(vaccinations)
                        .withStatus(HttpStatus.OK.value())));

        verifyNotPetsOfAnyStatusesAreAlreadyPresent();

        String newPetId = addANewPet();

        retrieveNewlyAddedPetById(newPetId);

        retrieveNewlyAddedPetByStatus(newPetId);

        updatePetDetails(newPetId);

        String generatedCustomerId = purchaseThePet(newPetId);

        verifyThePetHasBeenPurchased(generatedCustomerId, newPetId);
    }

    private void verifyThePetHasBeenPurchased(String customerId, String petId) {
        URI uri = uriBuilder.getPetStoreURIFor(petId)
                .build()
                .toUri();
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET,
                uri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = """
                      {
                        "vaccinations": [
                          {
                            "name": "Parainfluenza",
                            "dateOfAdminister": "2017-07-21"
                          },
                          {
                            "name": "Bordetella bronchiseptica",
                            "dateOfAdminister": "2017-09-05"
                          },
                          {
                            "name": "Canine Adenovirus",
                            "dateOfAdminister": "2016-01-25"
                          }
                        ],
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
        String actualCustomerId = JsonPath.read(response.getBody(), "$.owner.customerId");

        assertAll(
                assertions.assertOkJsonResponse(response),
                assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody),
                () -> assertEquals(petId, extractPetId(actualJsonBody)),
                () -> assertEquals(customerId, actualCustomerId)
        );
    }

    private String purchaseThePet(String petId) {
        URI uri = uriBuilder.getPetStoreURIFor(petId)
                .build()
                .toUri();
        RequestEntity<CustomerRequest> requestEntity = new RequestEntity<>(testData.createCustomerRequest(),
                HttpMethod.POST,
                uri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = """
                      {
                        "vaccinations": [
                          {
                            "name": "Parainfluenza",
                            "dateOfAdminister": "2017-07-21"
                          },
                          {
                            "name": "Bordetella bronchiseptica",
                            "dateOfAdminister": "2017-09-05"
                          },
                          {
                            "name": "Canine Adenovirus",
                            "dateOfAdminister": "2016-01-25"
                          }
                        ],
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
        String actualCustomerId = JsonPath.read(response.getBody(), "$.owner.customerId");

        assertAll(
                assertions.assertOkJsonResponse(response),
                assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody),
                () -> assertEquals(petId, extractPetId(actualJsonBody)),
                () -> assertThat(actualCustomerId, not(isEmptyOrNullString()))
        );

        return actualCustomerId;
    }

    private void updatePetDetails(String petId) {
        URI uri = uriBuilder.getPetStoreBaseURI()
                .build()
                .toUri();

        PetPatchRequest petPatch = new PetPatchRequest();
        petPatch.setId(petId);
        petPatch.setName("Astro");
        List<@Valid PetInformationItem> additionalInformation =
                Collections.singletonList(testData.createPetInformationItem("Eye colour", "Green"));
        petPatch.setAdditionalInformation(additionalInformation);

        RequestEntity<PetPatchRequest> requestEntity = new RequestEntity<>(petPatch, HttpMethod.PATCH, uri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = """
                 {
                   "vaccinations": [
                     {
                       "name": "Parainfluenza",
                       "dateOfAdminister": "2017-07-21"
                     },
                     {
                       "name": "Bordetella bronchiseptica",
                       "dateOfAdminister": "2017-09-05"
                     },
                     {
                       "name": "Canine Adenovirus",
                       "dateOfAdminister": "2016-01-25"
                     }
                   ],
                   "petStatus": "Available For Purchase",
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
                assertions.assertOkJsonResponse(response),
                assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody),
                () -> assertEquals(petId, extractPetId(actualJsonBody))
        );
    }

    private void retrieveNewlyAddedPetByStatus(String petId) {
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
                         "vaccinations": [
                           {
                             "name": "Parainfluenza",
                             "dateOfAdminister": "2017-07-21"
                           },
                           {
                             "name": "Bordetella bronchiseptica",
                             "dateOfAdminister": "2017-09-05"
                           },
                           {
                             "name": "Canine Adenovirus",
                             "dateOfAdminister": "2016-01-25"
                           }
                         ],
                         "petStatus": "Available For Purchase",
                         "vaccinationId": "AF54785412K",
                         "name": "Fido",
                         "petType": "Dog",
                         "photoUrls": [
                           "https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6"
                         ],
                         "additionalInformation": [
                           {
                             "name": "Personality",
                             "description": "Energetic"
                           }
                         ]
                       }
                     ]
                   }
                """;

        String actualJsonBody = response.getBody();

        assertAll(
                assertions.assertOkJsonResponse(response),
                assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody),
                () -> assertEquals(petId, JsonPath.read(actualJsonBody, "$.pets[0].petId"))
        );
    }

    private void retrieveNewlyAddedPetById(String petId) {
        URI uri = uriBuilder.getPetStoreURIFor(petId)
                .build()
                .toUri();
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET,
                uri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = """
                      {
                        "vaccinations": [
                          {
                            "name": "Parainfluenza",
                            "dateOfAdminister": "2017-07-21"
                          },
                          {
                            "name": "Bordetella bronchiseptica",
                            "dateOfAdminister": "2017-09-05"
                          },
                          {
                            "name": "Canine Adenovirus",
                            "dateOfAdminister": "2016-01-25"
                          }
                        ],
                        "petStatus": "Available For Purchase",
                        "vaccinationId": "AF54785412K",
                        "name": "Fido",
                        "petType": "Dog",
                        "photoUrls": [
                          "https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6"
                        ],
                        "additionalInformation": [
                          {
                            "name": "Personality",
                            "description": "Energetic"
                          }
                        ]
                      }
                """;

        String actualJsonBody = response.getBody();

        assertAll(
                assertions.assertOkJsonResponse(response),
                assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody),
                () -> assertEquals(petId, extractPetId(actualJsonBody))
        );
    }

    private String addANewPet() {
        NewPetRequest petToAdd = testData.createNewPetRequest();
        URI uri = uriBuilder.getPetStoreBaseURI()
                .build()
                .toUri();
        RequestEntity<NewPetRequest> requestEntity = new RequestEntity<>(petToAdd, HttpMethod.POST, uri);

        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = """
                     {
                       "vaccinations": [
                         {
                           "name": "Parainfluenza",
                           "dateOfAdminister": "2017-07-21"
                         },
                         {
                           "name": "Bordetella bronchiseptica",
                           "dateOfAdminister": "2017-09-05"
                         },
                         {
                           "name": "Canine Adenovirus",
                           "dateOfAdminister": "2016-01-25"
                         }
                       ],
                       "petStatus": "Available For Purchase",
                       "vaccinationId": "AF54785412K",
                       "name": "Fido",
                       "petType": "Dog",
                       "photoUrls": [
                         "https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6"
                       ],
                       "additionalInformation": [
                         {
                           "name": "Personality",
                           "description": "Energetic"
                         }
                       ]
                     }
                """;

        String actualJsonBody = response.getBody();
        String actualPetId = extractPetId(actualJsonBody);

        assertAll(
                assertions.assertOkJsonResponse(response),
                assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody),
                () -> assertThat(actualPetId, not(isEmptyOrNullString()))
        );

        return actualPetId;
    }

    private void verifyNotPetsOfAnyStatusesAreAlreadyPresent() {
        URI uri = uriBuilder.getPetStoreBaseURI()
                .pathSegment("findByStatus")
                .queryParam("statuses", ALL_PET_STATUSES)
                .build()
                .toUri();

        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET, uri);
        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = "{ }";
        String actualJsonBody = response.getBody();

        assertAll(
                assertions.assertOkJsonResponse(response),
                assertions.assertLenientJsonEquals(expectedJsonBody, actualJsonBody)
        );
    }

    private String extractPetId(String actualJsonBody) {
        return JsonPath.read(actualJsonBody, "$.petId");
    }
}
