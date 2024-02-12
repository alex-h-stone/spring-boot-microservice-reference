package com.cgi.example.petstore.integration;

import com.cgi.example.petstore.thirdparty.vaccinations.VaccinationsApiClient;
import com.cgi.example.thirdparty.animalvaccination.model.Vaccination;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VaccinationsApiClientIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private VaccinationsApiClient apiClient;

    @Test
    void shouldReturnVaccinationDetailsForValidVaccinationId() {
        String body = fileUtils.readFile("thirdparty\\animalvaccinationapi\\response\\vaccinationResponseMultiple.json");
        stubServer.stubFor(WireMock.get(urlEqualTo("/vaccinations/AF54785412K"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(body)
                        .withStatus(HttpStatus.OK.value())));

        String validVaccinationId = "AF54785412K";

        Optional<List<Vaccination>> actualResponse = apiClient.getVaccinations(validVaccinationId);

        assertTrue(actualResponse.isPresent());
        List<Vaccination> vaccinations = actualResponse.get();
        assertThat(vaccinations, Matchers.iterableWithSize(3));
    }

    @Test
    void shouldReturnEmptyOptionalForInvalidVaccinationId() {
        Optional<List<Vaccination>> optionalVaccinations = apiClient.getVaccinations("Z6456INVALID");

        assertTrue(optionalVaccinations.isEmpty());
    }

    @Test
    void shouldRetryIfTheFirstRequestFails() {
        // TODO verify this works correctly
        String body = fileUtils.readFile("thirdparty\\animalvaccinationapi\\response\\vaccinationResponseMultiple.json");
        ResponseDefinitionBuilder responseDefBuilder = aResponse()
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(body)
                .withStatus(HttpStatus.OK.value());

        stubServer.stubFor(WireMock.get(urlEqualTo("/vaccinations/AF54785412K"))
                .willReturn(aResponse().withStatus(HttpStatus.GATEWAY_TIMEOUT.value()))
                .willReturn(responseDefBuilder));

        Optional<List<Vaccination>> actualResponse = apiClient.getVaccinations("AF54785412K");

        assertTrue(actualResponse.isPresent());
        List<Vaccination> vaccinations = actualResponse.get();
        assertThat(vaccinations, Matchers.iterableWithSize(3));
    }
}