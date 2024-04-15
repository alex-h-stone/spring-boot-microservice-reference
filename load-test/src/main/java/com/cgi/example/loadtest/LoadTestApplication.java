package com.cgi.example.loadtest;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import lombok.extern.slf4j.Slf4j;
import scala.collection.mutable.Map;

@Slf4j
public class LoadTestApplication {

    public static void main(String[] args) {
        LoadTestApplication loadTestApplication = new LoadTestApplication();
        loadTestApplication.run();
    }

    private void run() {
        String canonicalName = LoadSimulationDefinition.class.getCanonicalName();
        Map<String, Object> gatlingProperties = new GatlingPropertiesBuilder()
                .simulationClass(canonicalName)
                .resultsDirectory("build/load-test-results")
                .runDescription("Pet Store load testing")
                .build();

        Gatling.fromMap(gatlingProperties);
    }
}
