package com.cgi.example.petstore.utils;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public Executable assertJSONContentType(ResponseEntity<?> response) {
        return assertContentType(response, MediaType.APPLICATION_JSON_VALUE);
    }

    public Executable assertContentType(HttpEntity<?> response, String expectedContentType) {
        return () -> {
            List<String> contentTypes = response.getHeaders().get(HttpHeaders.CONTENT_TYPE);

            assertThat(contentTypes, Matchers.equalTo(List.of(expectedContentType)));
        };
    }

    public void assertOkJsonResponse(ResponseEntity<?> response) {
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                assertJSONContentType(response)
        );
    }
}
