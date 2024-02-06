package com.cgi.example.petstore.integration.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class LoggingTestRestTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingTestRestTemplate.class);

    private final TestRestTemplate testRestTemplate;

    public LoggingTestRestTemplate(TestRestTemplate testRestTemplate) {
        this.testRestTemplate = testRestTemplate;
    }

    public ResponseEntity<String> execute(RequestEntity<?> requestEntity) {
        LOG.info("Integration test RequestEntity: [{}]", requestEntity);
        ResponseEntity<String> response = testRestTemplate.exchange(requestEntity, String.class);
        LOG.info("Integration test ResponseEntity: [{}]", response);
        return response;
    }
}
