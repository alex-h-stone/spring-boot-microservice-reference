package com.cgi.example.loadtest;

import com.cgi.example.loadtest.memory.MemoryUsageMetrics;
import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpDsl;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.http.HttpDsl.status;

@Slf4j
public class LoadSimulationDefinition extends Simulation {

    private static final Duration TIME_TO_RUN = Duration.ofSeconds(10);
    private static final String ADD_PET_REQUEST_BODY_JSON = """
            {
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

    private final HttpProtocolBuilders protocolBuilders = new HttpProtocolBuilders();
    private final MemoryUsageMetrics memoryUsageMetrics = new MemoryUsageMetrics();

    private final HttpRequestActionBuilder addPet = HttpDsl.http("Add Pet")
            .post("/api/v1/pet-store/pets")
            .body(StringBody(ADD_PET_REQUEST_BODY_JSON)).asJson()
            .check(status().is(200))
            .check(jsonPath("$.petId").saveAs("petId"));

    private final HttpRequestActionBuilder getPet = HttpDsl.http("Get Pet")
            .get("/api/v1/pet-store/pets/${petId}")
            .check(status().is(200));

    private final HttpRequestActionBuilder deletePet = HttpDsl.http("Delete Pet")
            .delete("/api/v1/pet-store/pets/${petId}")
            .check(status().is(200));

    private final ScenarioBuilder applicationScenario = CoreDsl.scenario("Application scenario")
            .exec(addPet)
            .exec(getPet)
            .exec(deletePet);

    private final PopulationBuilder applicationPopulation = applicationScenario.injectOpen(
                    CoreDsl.constantUsersPerSec(10).during(TIME_TO_RUN))
            .protocols(protocolBuilders.createApplicationProtocol());

    private final PopulationBuilder memoryUsagePopulation = memoryUsageMetrics.createMemoryMetricsPopulation(
            protocolBuilders.createManagementProtocol(),
            TIME_TO_RUN);

    {
        setUp(applicationPopulation, memoryUsagePopulation);
    }

    @Override
    public void before() {
        memoryUsageMetrics.beforeCallback();
    }

    @Override
    public void after() {
        memoryUsageMetrics.afterCallback();
    }
}