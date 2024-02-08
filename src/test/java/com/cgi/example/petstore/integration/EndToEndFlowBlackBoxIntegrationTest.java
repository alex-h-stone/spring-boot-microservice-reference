package com.cgi.example.petstore.integration;

import com.cgi.example.petstore.model.NewPet;
import com.cgi.example.petstore.model.PetStatus;
import com.cgi.example.petstore.utils.TestData;
import com.github.tomakehurst.wiremock.client.WireMock;
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
import java.util.List;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EndToEndFlowBlackBoxIntegrationTest extends BaseIntegrationTest {

    private static final List<String> ALL_PET_STATUSES = Arrays.stream(PetStatus.values())
            .map(Enum::name)
            .collect(Collectors.toList());

    private final TestData testData = new TestData();

    @Test
    void shouldBeABleToAddAPet() {
        String body = fileUtils.readFile("thirdparty\\animalvaccinationapi\\response\\vaccinationResponseMultiple.json");
        stubServer.stubFor(WireMock.get(urlEqualTo("/vaccinations/AF54785412K"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(body)
                        .withStatus(HttpStatus.OK.value())));

        verifyNotPetsOfAnyStatusesAreAlreadyPresent();

        addANewPet();

        retrieveNewlyAddedPetById();

        retrieveNewlyAddedPetByStatus();

        updatePetDetails();

        verifyThePetHasBeenUpdated();

        purchaseThePet();

        verifyThePetHasBeenPurchased();
    }

    private void verifyThePetHasBeenPurchased() {

    }

    private void purchaseThePet() {

    }

    private void verifyThePetHasBeenUpdated() {

    }

    private void updatePetDetails() {
        // TODO i nclude extra information and changfe somehting
    }

    private void retrieveNewlyAddedPetByStatus() {
        // TODO
    }

    private void retrieveNewlyAddedPetById() {
        // TODO
    }

    private void addANewPet() {
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
                       "id": 10,
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
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                assertions.assertJSONContentType(response),
                () -> JSONAssert.assertEquals(expectedJsonBody, actualJsonBody, JSONCompareMode.LENIENT)
        );
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
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                assertions.assertJSONContentType(response),
                () -> JSONAssert.assertEquals(expectedJsonBody, actualJsonBody, JSONCompareMode.LENIENT)
        );
    }
}
