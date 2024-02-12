package com.cgi.example.petstore.integration;

import com.cgi.example.petstore.model.Customer;
import com.cgi.example.petstore.model.NewPet;
import com.cgi.example.petstore.model.PetInformationItem;
import com.cgi.example.petstore.model.PetPatch;
import com.cgi.example.petstore.model.PetStatus;
import com.cgi.example.petstore.utils.TestData;
import com.github.tomakehurst.wiremock.client.WireMock;
import jakarta.validation.Valid;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
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

public class EndToEndFlowBlackBoxIntegrationTest extends BaseIntegrationTest {

    private static final List<String> ALL_PET_STATUSES = Arrays.stream(PetStatus.values())
            .map(Enum::name)
            .collect(Collectors.toList());

    private final TestData testData = new TestData();

    @Test
    void shouldSuccessfullyAddModifyFindUpdateAndPurchaseAPet() throws JSONException {
        String vaccinations = fileUtils.readFile("thirdparty\\animalvaccinationapi\\response\\vaccinationResponseMultiple.json");
        stubServer.stubFor(WireMock.get(urlEqualTo("/vaccinations/AF54785412K"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(vaccinations)
                        .withStatus(HttpStatus.OK.value())));

        verifyNotPetsOfAnyStatusesAreAlreadyPresent();

        addANewPet();

        retrieveNewlyAddedPetById();

        retrieveNewlyAddedPetByStatus();

        updatePetDetails();

        purchaseThePet();

        verifyThePetHasBeenPurchased();
    }

    private void verifyThePetHasBeenPurchased() throws JSONException {
        URI uri = uriBuilder.getPetStoreURIFor(String.valueOf(10L))
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

        assertions.assertOkJsonResponse(response);
        JSONAssert.assertEquals(expectedJsonBody, actualJsonBody, JSONCompareMode.LENIENT);
    }

    private void purchaseThePet() throws JSONException {
        URI uri = uriBuilder.getPetStoreURIFor(String.valueOf(10L))
                .build()
                .toUri();
        RequestEntity<Customer> requestEntity = new RequestEntity<>(testData.createCustomer(),
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

        assertions.assertOkJsonResponse(response);
        JSONAssert.assertEquals(expectedJsonBody, actualJsonBody, JSONCompareMode.LENIENT);
    }

    private void updatePetDetails() throws JSONException {
        URI uri = uriBuilder.getPetStoreBaseURI()
                .build()
                .toUri();

        PetPatch petPatch = new PetPatch();
        petPatch.setId(10L);
        petPatch.setName("Astro");
        List<@Valid PetInformationItem> additionalInformation =
                Collections.singletonList(testData.createPetInformationItem("Eye colour", "Green"));
        petPatch.setAdditionalInformation(additionalInformation);

        RequestEntity<PetPatch> requestEntity = new RequestEntity<>(petPatch, HttpMethod.PATCH, uri);

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
                   "petId": 10,
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

        assertions.assertOkJsonResponse(response);
        JSONAssert.assertEquals(expectedJsonBody, actualJsonBody, JSONCompareMode.LENIENT);
    }

    private void retrieveNewlyAddedPetByStatus() throws JSONException {
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
                         "petId": 10,
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

        assertions.assertOkJsonResponse(response);
        JSONAssert.assertEquals(expectedJsonBody, actualJsonBody, JSONCompareMode.LENIENT);
    }

    private void retrieveNewlyAddedPetById() throws JSONException {
        URI uri = uriBuilder.getPetStoreURIFor(String.valueOf(10L))
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
                        "petId": 10,
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

        assertions.assertOkJsonResponse(response);
        JSONAssert.assertEquals(expectedJsonBody, actualJsonBody, JSONCompareMode.LENIENT);
    }

    private void addANewPet() throws JSONException {
        NewPet petToAdd = testData.createNewPet();
        URI uri = uriBuilder.getPetStoreBaseURI()
                .build()
                .toUri();
        RequestEntity<NewPet> requestEntity = new RequestEntity<>(petToAdd, HttpMethod.POST, uri);

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
                       "petId": 10,
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

        assertions.assertOkJsonResponse(response);
        JSONAssert.assertEquals(expectedJsonBody, actualJsonBody, JSONCompareMode.LENIENT);
    }

    private void verifyNotPetsOfAnyStatusesAreAlreadyPresent() throws JSONException {
        URI uri = uriBuilder.getPetStoreBaseURI()
                .pathSegment("findByStatus")
                .queryParam("statuses", ALL_PET_STATUSES)
                .build()
                .toUri();

        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.GET, uri);
        ResponseEntity<String> response = testRestTemplate.execute(requestEntity);

        String expectedJsonBody = "{ }";
        String actualJsonBody = response.getBody();

        assertions.assertOkJsonResponse(response);
        JSONAssert.assertEquals(expectedJsonBody, actualJsonBody, JSONCompareMode.LENIENT);
    }
}
