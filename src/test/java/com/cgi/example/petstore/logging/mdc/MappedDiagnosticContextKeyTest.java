package com.cgi.example.petstore.logging.mdc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.MDC;
import wiremock.org.hamcrest.Matchers;

class MappedDiagnosticContextKeyTest {

  private static final Consumer<MappedDiagnosticContextKey> ASSERT_MDC_VALUE_IS_NULL =
      key -> {
        String actualMdcValue = MDC.get(key.getMdcKey());
        String message =
            "Expected null MDC value for key [%s] but found [%s]".formatted(key, actualMdcValue);
        assertNull(actualMdcValue, message);
      };

  @BeforeEach
  void beforeEach() {
    MDC.clear();
  }

  @AfterEach
  void afterEach() {
    MDC.clear();
  }

  @ParameterizedTest
  @MethodSource("allMappedDiagnosticContextKeys")
  void should_StoreValueInTheMdc_whenCallingPut(MappedDiagnosticContextKey key) {
    assertNull(MDC.get(key.getMdcKey()), "Failed precondition for key: " + key);
    final String expectedNewValue = "newValue";

    key.put(expectedNewValue);

    assertEquals(expectedNewValue, MDC.get(key.getMdcKey()));
  }

  @ParameterizedTest
  @MethodSource("allMappedDiagnosticContextKeys")
  void allMdcKeysShouldBeNonNullAndNotEmpty(MappedDiagnosticContextKey key) {
    String actualMdcKey = key.getMdcKey();

    assertThat(actualMdcKey, not(Matchers.blankOrNullString()));
  }

  @Test
  void clearAll_should_clearAllMdcKeys() {
    allMappedDiagnosticContextKeys().forEach(ASSERT_MDC_VALUE_IS_NULL);

    allMappedDiagnosticContextKeys().forEach(key -> key.put("newValue"));

    MappedDiagnosticContextKey.clearAll();

    allMappedDiagnosticContextKeys().forEach(ASSERT_MDC_VALUE_IS_NULL);
  }

  static Stream<MappedDiagnosticContextKey> allMappedDiagnosticContextKeys() {
    return Arrays.stream(MappedDiagnosticContextKey.values());
  }
}
