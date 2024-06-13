package com.cgi.example.petstore.utils.logging;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.LoggerFactory;

/**
 * This class should be used in a JUnit test by including the class level annotations:<br>
 * {@code @ExtendWith(LoggingVerificationJUnitExtension.class)}<br>
 * {@code @LoggingVerificationTarget(MappedDiagnosticContextKey.class)}<br>
 * Where {@code MappedDiagnosticContextKey.class} is the class under test.
 *
 * <p>Verification of logging events can then be done by calling {@code
 * LoggingVerification.assertLog()}.
 *
 * <p>e.g.<br>
 * {@code LoggingVerification.assertLog(Level.DEBUG, Matchers.equalTo("Clearing all MDC keys")}
 */
@Disabled
public class LoggingVerificationJUnitExtension implements BeforeEachCallback, AfterEachCallback {

  private static LoggingListAppender listAppender;
  private Logger testLogger;

  @Override
  public void afterEach(ExtensionContext context) {
    testLogger.detachAppender(listAppender);
    listAppender.stop();
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    LoggingVerificationTarget loggingTarget =
        context.getRequiredTestClass().getAnnotation(LoggingVerificationTarget.class);
    assertNotNull(
        loggingTarget,
        "Expected non-null test logging target to be defined by the test class annotation @%s()"
            .formatted(LoggingVerificationTarget.class.getSimpleName()));

    testLogger = (Logger) LoggerFactory.getLogger(loggingTarget.value());
    testLogger.setLevel(Level.DEBUG);

    listAppender = new LoggingListAppender();
    listAppender.start();

    testLogger.addAppender(listAppender);
  }

  static List<ILoggingEvent> getLogEvents() {
    return List.copyOf(listAppender.getLogEvents());
  }

  @Getter
  @Slf4j
  private static final class LoggingListAppender extends AppenderBase<ILoggingEvent> {
    private final List<ch.qos.logback.classic.spi.ILoggingEvent> logEvents =
        new CopyOnWriteArrayList<>();

    public LoggingListAppender() {
      setName("LoggingVerificationListAppender");
    }

    @Override
    protected void append(ILoggingEvent loggingEvent) {
      log.info("Appending the Logging Event: {}", loggingEvent);
      logEvents.add(loggingEvent);
    }
  }
}
