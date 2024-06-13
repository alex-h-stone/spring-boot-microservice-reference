package com.cgi.example.petstore.utils.logging;

import static org.junit.jupiter.api.Assertions.fail;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.List;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Disabled;

@Disabled
public class LoggingVerification {

  /**
   * Assert a matching log event occurred or otherwise {@code fail}.
   *
   * @param expectedLogLevel e.g. {@code Level.INFO} where {@code Level} is the expected logging
   *     level from {@code ch.qos.logback.classic.Level}
   * @param logMessageMatcher e.g. {@code Matchers.equalTo("Expected log message")} A Hamcrest
   *     {@code Matcher<String>} to match the expected log message.
   */
  public static void assertLog(Level expectedLogLevel, Matcher<String> logMessageMatcher) {
    List<ILoggingEvent> allLogEvents = LoggingVerificationJUnitExtension.getLogEvents();
    List<ILoggingEvent> matchingLogLevel =
        allLogEvents.stream().filter(event -> event.getLevel().equals(expectedLogLevel)).toList();

    List<ILoggingEvent> matchingLogEvents =
        matchingLogLevel.stream()
            .filter(event -> logMessageMatcher.matches(event.getFormattedMessage()))
            .toList();

    if (matchingLogEvents.isEmpty()) {
      String message =
          "Unable to find a %s log event with message matching %s in %s"
              .formatted(expectedLogLevel, logMessageMatcher, allLogEvents);
      fail(message);
    }

    if (matchingLogEvents.size() > 1) {
      String message =
          "Found %d %s log events with message matching %s, but expected 1, in %s"
              .formatted(
                  matchingLogEvents.size(), expectedLogLevel, logMessageMatcher, allLogEvents);
      fail(message);
    }
  }
}
