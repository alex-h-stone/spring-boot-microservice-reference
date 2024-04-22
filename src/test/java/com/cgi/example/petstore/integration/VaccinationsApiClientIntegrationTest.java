package com.cgi.example.petstore.integration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cgi.example.external.animalvaccination.model.Vaccination;
import com.cgi.example.petstore.external.vaccinations.VaccinationsApiClient;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.PathTemplatePattern;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import java.util.List;
import java.util.Optional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@Tag("integration")
class VaccinationsApiClientIntegrationTest extends BaseIntegrationTest {

  @Autowired private VaccinationsApiClient apiClient;

  @Test
  void should_ReturnVaccinationDetailsForValidVaccinationId() {
    stubServer.stubFor(
        WireMock.get(urlEqualTo("/vaccinations/AF54785412K")).willReturn(successResponse()));

    String validVaccinationId = "AF54785412K";

    Optional<List<Vaccination>> actualResponse = apiClient.getVaccinations(validVaccinationId);

    assertTrue(actualResponse.isPresent());
    List<Vaccination> vaccinations = actualResponse.get();
    assertThat(vaccinations, Matchers.iterableWithSize(3));
  }

  @Test
  void should_ReturnEmptyOptionalForUnknownVaccinationId() {
    stubServer.stubFor(
        WireMock.get(urlEqualTo("/vaccinations/Z6456INVALID"))
            .willReturn(
                aResponse()
                    .withHeader("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .withBody(
                        """
                                {
                                  "type": "about:blank",
                                  "title": "Not Found",
                                  "status": 404,
                                  "detail": "Unable to find vaccinations for Id [Z6456INVALID]"
                                }
                                """)
                    .withStatus(HttpStatus.NOT_FOUND.value())));

    Optional<List<Vaccination>> optionalVaccinations = apiClient.getVaccinations("Z6456INVALID");

    assertTrue(optionalVaccinations.isEmpty());
  }

  @Test
  void should_RetryTwiceIfTheRequestFailsBeforeEventuallyFailing() {
    stubServer.stubFor(
        WireMock.get(urlEqualTo("/vaccinations/AF54785412K"))
            .willReturn(aResponse().withStatus(HttpStatus.GATEWAY_TIMEOUT.value())));

    Optional<List<Vaccination>> actualResponse = apiClient.getVaccinations("AF54785412K");

    assertTrue(actualResponse.isEmpty());
    UrlPattern url = new UrlPattern(new PathTemplatePattern("/vaccinations/AF54785412K"), false);
    stubServer.verify(3, newRequestPattern(RequestMethod.GET, url));
  }

  private ResponseDefinitionBuilder successResponse() {
    String body =
        fileUtils.readFile(
            "external\\animalvaccinationapi\\response\\vaccinationResponseMultiple.json");

    return aResponse()
        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        .withBody(body)
        .withStatus(HttpStatus.OK.value());
  }

  @Test
  void should_RetryTwiceIfTheRequestFailsBeforeEventuallySucceeding() {
    final String scenarioName = "RetryUntilSuccess";
    stubServer.stubFor(
        WireMock.get(urlEqualTo("/vaccinations/AF54785412K"))
            .inScenario(scenarioName)
            .willSetStateTo("Second Call")
            .willReturn(aResponse().withStatus(HttpStatus.GATEWAY_TIMEOUT.value())));

    stubServer.stubFor(
        WireMock.get(urlEqualTo("/vaccinations/AF54785412K"))
            .inScenario(scenarioName)
            .whenScenarioStateIs("Second Call")
            .willSetStateTo("Third Call")
            .willReturn(aResponse().withStatus(HttpStatus.GATEWAY_TIMEOUT.value())));

    String body =
        fileUtils.readFile(
            "external\\animalvaccinationapi\\response\\vaccinationResponseMultiple.json");

    stubServer.stubFor(
        WireMock.get(urlEqualTo("/vaccinations/AF54785412K"))
            .inScenario(scenarioName)
            .whenScenarioStateIs("Third Call")
            .willReturn(successResponse()));

    Optional<List<Vaccination>> actualResponse = apiClient.getVaccinations("AF54785412K");

    assertTrue(actualResponse.isPresent());
    List<Vaccination> vaccinations = actualResponse.get();
    assertThat(vaccinations, Matchers.iterableWithSize(3));
    UrlPattern url = new UrlPattern(new PathTemplatePattern("/vaccinations/AF54785412K"), false);
    stubServer.verify(3, newRequestPattern(RequestMethod.GET, url));
  }
}
