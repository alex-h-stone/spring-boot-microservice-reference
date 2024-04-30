package com.cgi.example.apitest;

import com.cgi.example.common.local.DynamicApplicationPropertiesRepository;
import com.cgi.example.common.local.ToClickableUriString;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class ApiTestApplication {

    private static final String POSTMAN_COLLECTION = "src/main/resources/spring-boot-microservice-template.postman_collection.json";
    private static final String POSTMAN_ENVIRONMENT = "src/main/resources/local.postman_environment.json";

    private final DynamicApplicationPropertiesRepository propertiesRepository = new DynamicApplicationPropertiesRepository();
    private final ToClickableUriString toClickableUriString = new ToClickableUriString();

    public static void main(String[] args) {
        ApiTestApplication apiTestApplication = new ApiTestApplication();
        apiTestApplication.start();
    }

    private void start() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.redirectErrorStream(true);
        File reportFile = reportFile();

        processBuilder.command("cmd", "/c", "newman",
                "run", POSTMAN_COLLECTION,
                "--reporters", "cli,htmlextra,json",
                "--reporter-htmlextra-export", reportFile.getAbsolutePath(),
                "--environment", POSTMAN_ENVIRONMENT,
                "--env-var", "applicationPort=" + propertiesRepository.getApplicationPort(),
                "--env-var", "managementPort=" + propertiesRepository.getManagementPort(),
                "--env-var", "wireMockPort=" + propertiesRepository.getWireMockPort(),
                "--env-var", "oAuth2Port=" + propertiesRepository.getOAuth2Port());

        Process process = startProcess(processBuilder);

        int exitCode = waitForProcessToComplete(process);

        log.info("API tests completed with error code: {}", exitCode);
        log.info("API test report has been saved to: {}", toClickableUriString.apply(reportFile));
        if (exitCode != 0) {
            System.err.println("API test failure, see the report: " + toClickableUriString.apply(reportFile));
        }
        System.exit(exitCode);
    }


    private File reportFile() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        String dateTime = dateTimeFormatter.format(LocalDateTime.now());

        String relativePath = "build/newman/api-test-report-%s.html".formatted(dateTime);

        return Paths.get(relativePath).toFile();
    }

    private int waitForProcessToComplete(Process process) {
        try {
            return process.waitFor();
        } catch (InterruptedException e) {
            log.error("InterruptedException thrown when executing API tests: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private Process startProcess(ProcessBuilder processBuilder) {
        try {
            log.info("Starting API tests");
            Process started = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(started.getInputStream()))) {
                reader.lines().forEach(log::info);
            }
            return started;
        } catch (IOException e) {
            log.error("IOException thrown when executing API tests: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
