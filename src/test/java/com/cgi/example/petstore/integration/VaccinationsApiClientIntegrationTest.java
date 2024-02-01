package com.cgi.example.petstore.integration;

import com.cgi.example.petstore.thirdparty.vaccinations.VaccinationsApiClient;
import com.cgi.example.thirdparty.animalvaccination.model.VaccinationsResponse;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientException;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VaccinationsApiClientIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private VaccinationsApiClient apiClient;

    @BeforeEach
    void beforeEach() {
        String body = fileUtils.readFile("thirdparty\\animalvaccinationapi\\response\\vaccinationResponseMultiple.json");
        stubServer.stubFor(WireMock.get(urlEqualTo("/vaccinations/AF54785412K"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(body)
                        .withStatus(HttpStatus.OK.value())));
    }

    @Test
    void shouldReturnVaccinationDetailsForValidVaccinationId() {
        String validVaccinationId = "AF54785412K";
        ResponseEntity<VaccinationsResponse> actualResponse = apiClient.getVaccinationDetails(validVaccinationId);

        VaccinationsResponse vaccinationsResponseBody = actualResponse.getBody();

        assertAll(
                () -> assertEquals(HttpStatus.OK, actualResponse.getStatusCode()),
                assertions.assertJSONContentType(actualResponse),
                () -> assertNotNull(vaccinationsResponseBody),
                () -> assertEquals(validVaccinationId, vaccinationsResponseBody.getVaccinationId()),
                () -> assertThat(vaccinationsResponseBody.getVaccinations(), Matchers.iterableWithSize(3)));
    }

    @Test
    void shouldReturnExceptionForInvalidVaccinationId() {
        WebClientException expectedException = assertThrows(WebClientException.class,
                () -> apiClient.getVaccinationDetails("Z6456INVALID"));

        assertAll(
                () -> assertThat(expectedException.getMessage(), CoreMatchers.containsString("404 Not Found from GET")),
                () -> assertThat(expectedException.getMessage(), CoreMatchers.containsString("vaccinations/Z6456INVALID")));
    }
}