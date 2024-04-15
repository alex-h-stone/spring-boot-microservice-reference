package com.cgi.example.petstore.utils;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class ProcessManagementTest {

  @Test
  void shouldTimeOutAfterThreeSecondsIsTaskHasNotCompleted() {
    assertTimeoutPreemptively(
        Duration.ofSeconds(5), () -> ProcessManagement.waitUntil(() -> false));
  }

  @Test
  void shouldCompleteInLessThanTwoSecondsOnImmediateSuccess() {
    assertTimeoutPreemptively(Duration.ofSeconds(2), () -> ProcessManagement.waitUntil(() -> true));
  }
}
