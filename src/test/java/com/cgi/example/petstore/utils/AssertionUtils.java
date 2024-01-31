package com.cgi.example.petstore.utils;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    private static final String LINE_BREAKS = "\n+";
    private static final String DOUBLE_SPACES = "\\s+";

    public Executable assertJSONContentType(ResponseEntity<?> response) {
        return assertContentType(response, MediaType.APPLICATION_JSON_VALUE);
    }

    public Executable assertContentType(ResponseEntity<?> response, String expectedContentType) {
        return () -> {
            List<String> contentTypes = response.getHeaders().get(HttpHeaders.CONTENT_TYPE);

            assertThat(contentTypes, Matchers.equalTo(List.of(expectedContentType)));
        };
    }

    public void assertEqualsWithNormalisedSpaces(String expected, String actual) {
        assertEquals(normalizeSpaces(expected), normalizeSpaces(actual));
    }

    private String normalizeSpaces(String inputToNormalise) {
        return inputToNormalise.replaceAll(LINE_BREAKS, StringUtils.SPACE)
                .replaceAll(DOUBLE_SPACES, StringUtils.SPACE)
                .trim();
    }
}
