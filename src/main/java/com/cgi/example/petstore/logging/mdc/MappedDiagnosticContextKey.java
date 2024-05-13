package com.cgi.example.petstore.logging.mdc;

import java.util.Arrays;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Getter
@Slf4j
public enum MappedDiagnosticContextKey {
  REQUEST_ID("requestId"),
  USERNAME("username");

  private final String mdcKey;

  MappedDiagnosticContextKey(String mdcKey) {
    this.mdcKey = mdcKey;
  }

  public static void clearAll() {
    log.debug("Clearing all MDC keys");
    Arrays.stream(values())
        .forEach(mappedDiagnosticContextKey -> MDC.remove(mappedDiagnosticContextKey.mdcKey));
  }

  public void put(String value) {
    MDC.put(mdcKey, value);
    log.debug("Added the MDC key {} with a value of {}", mdcKey, value);
  }
}
