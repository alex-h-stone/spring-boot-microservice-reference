package com.cgi.example.petstore.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import ch.qos.logback.classic.Level;
import com.cgi.example.petstore.utils.LoggingVerification;
import com.cgi.example.petstore.utils.TestLoggingTarget;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

@Tag("unit")
@ExtendWith(LoggingVerification.class)
@TestLoggingTarget(PetStoreSystemProperty.class)
public class PetStoreSystemPropertyTest {

  @BeforeEach
  public void setup() {
    Arrays.stream(PetStoreSystemProperty.values()).forEach(PetStoreSystemProperty::clear);
  }

  @AfterEach
  public void cleanup() {
    Arrays.stream(PetStoreSystemProperty.values()).forEach(PetStoreSystemProperty::clear);
  }

  @ParameterizedTest
  @EnumSource(PetStoreSystemProperty.class)
  public void testGetExistingProperty(PetStoreSystemProperty property) {
    System.setProperty(property.toString(), "test-value");

    assertEquals("test-value", property.get(), "Property should match the set value.");
  }

  @ParameterizedTest
  @EnumSource(PetStoreSystemProperty.class)
  public void testGetNonExistingProperty(PetStoreSystemProperty property) {
    assertNull(property.get(), "Should return null for non-set properties.");
  }

  @ParameterizedTest
  @CsvSource({
    "MONGO_DB_URI, uri-value",
    "OAUTH_CLIENT_ID, client-id-123",
    "OAUTH_CLIENT_SECRET, secret-value",
    "OAUTH_HOST, http://localhost:8000",
    "VACCINATIONS_URL, http://localhost:9050"
  })
  public void testSetSystemPropertyIfAbsentNotSet(PetStoreSystemProperty property, String value) {
    property.setSystemPropertyIfAbsent(value);

    assertEquals(value, property.get(), "Property should be set to the provided value.");

    String expectedLogMessage =
        "The system property %s has been set with the value [%s]".formatted(property, value);
    LoggingVerification.assertLog(Level.INFO, Matchers.equalTo(expectedLogMessage));
  }

  @ParameterizedTest
  @CsvSource({
    "MONGO_DB_URI, uri-value",
    "OAUTH_CLIENT_ID, client-id-123",
    "OAUTH_CLIENT_SECRET, secret-value",
    "OAUTH_HOST, http://localhost:8000",
    "VACCINATIONS_URL, http://localhost:9050"
  })
  public void testSetSystemPropertyIfAbsentAlreadySet(
      PetStoreSystemProperty property, String newValue) {
    final String existingValue = "existing-value";
    System.setProperty(property.name(), existingValue);
    property.setSystemPropertyIfAbsent(newValue);

    assertEquals(existingValue, property.get(), "Property should retain the original value.");

    String expectedLogMessage =
        "Not setting system property %s as it has already been set with a value of [%s]"
            .formatted(property, existingValue);
    LoggingVerification.assertLog(Level.INFO, Matchers.equalTo(expectedLogMessage));
  }
}
