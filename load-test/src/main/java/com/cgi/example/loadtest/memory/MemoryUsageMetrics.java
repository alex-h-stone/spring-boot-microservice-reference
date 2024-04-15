package com.cgi.example.loadtest.memory;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.ProtocolBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.http.HttpDsl;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class MemoryUsageMetrics {

    private final ConcurrentLinkedQueue<MemoryUsageMetric> memoryUsageMetricsQueue = new ConcurrentLinkedQueue<>();

    public void beforeCallback() {
        memoryUsageMetricsQueue.clear();
    }

    public void afterCallback() {
        log.info("Collected {} metrics data points", memoryUsageMetricsQueue.size());
        DescriptiveStatistics descriptiveStatistics = new DescriptiveStatistics();
        memoryUsageMetricsQueue.stream()
                .map(memoryUsageMetric -> memoryUsageMetric.getMemoryUsedInBytes().doubleValue())
                .forEach(descriptiveStatistics::addValue);

        double minimumMemoryUsage = descriptiveStatistics.getMin();
        double maximumMemoryUsage = descriptiveStatistics.getMax();
        logAtInfoLevel("min", minimumMemoryUsage);
        logAtInfoLevel("max", maximumMemoryUsage);
        logAtInfoLevel("mean", descriptiveStatistics.getMean());

        if (maximumMemoryUsage > 10.0 * minimumMemoryUsage) {
            String message = "Maximum memory usage %s is over 10 times the minimum memory usage %s"
                    .formatted(maximumMemoryUsage, minimumMemoryUsage);
            throw new IllegalStateException(message);
        }
    }

    private void logAtInfoLevel(String statisticName, double mean) {
        log.info("Memory usage {}: {}", statisticName, toMegaBytes(mean));
    }

    private String toMegaBytes(double amountInBytes) {
        double amountInMegaBytes = amountInBytes / (1024.0 * 1024.0);
        DecimalFormat withThousandSeparators = new DecimalFormat("#,##0");

        return withThousandSeparators.format(amountInMegaBytes) + "MB";
    }

    public PopulationBuilder createMemoryMetricsPopulation(ProtocolBuilder managementHost,
                                                           Duration timeToRun) {
        ScenarioBuilder getMemoryUsageScenario = CoreDsl.scenario("Memory Usage scenario")
                .exec(HttpDsl.http("Get Memory Usage")
                        .get("/actuator/metrics/jvm.memory.used")
                        .check(HttpDsl.status().is(HttpResponseStatus.OK.code()))
                        .check(CoreDsl.jsonPath("$.name").is("jvm.memory.used"))
                        .check(CoreDsl.jsonPath("$.baseUnit").is("bytes"))
                        .check(CoreDsl.jsonPath("$.measurements[?(@.statistic == 'VALUE')].value").saveAs("memoryUsedInBytes"))
                )
                .exec(session -> {
                    String memoryUsedInBytes = session.getString("memoryUsedInBytes");
                    MemoryUsageMetric memoryUsageMetric = new MemoryUsageMetric(memoryUsedInBytes);
                    log.debug("Recorded memoryUsageMetric: [{}]", memoryUsageMetric);
                    memoryUsageMetricsQueue.add(memoryUsageMetric);
                    return session;
                });

        return getMemoryUsageScenario.injectOpen(
                        CoreDsl.constantUsersPerSec(1)
                                .during(timeToRun))
                .protocols(managementHost);
    }
}
