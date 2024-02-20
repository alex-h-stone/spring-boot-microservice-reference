package com.cgi.example.petstore.utils;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.function.Executable;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionExecutables {

    public Executable assertContentType(HttpEntity<?> response, String expectedContentType) {
        return () -> {
            List<String> contentTypes = response.getHeaders().get(HttpHeaders.CONTENT_TYPE);

            assertThat(contentTypes, Matchers.equalTo(List.of(expectedContentType)));
        };
    }

    public Executable assertJsonContentType(ResponseEntity<?> response) {
        return assertContentType(response, MediaType.APPLICATION_JSON_VALUE);
    }

    public Executable assertProblemJsonContentType(ResponseEntity<?> response) {
        return assertContentType(response, MediaType.APPLICATION_PROBLEM_JSON_VALUE);
    }

    public Executable assertOkJsonResponse(ResponseEntity<?> response) {
        return () -> {
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertJsonContentType(response).execute();
        };
    }

    public Executable assertLenientJsonEquals(String expectedJson, String actualJson) {
        return () -> JSONAssert.assertEquals(expectedJson, actualJson, JSONCompareMode.LENIENT);
    }
}
