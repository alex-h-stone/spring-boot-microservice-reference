package com.cgi.example.petstore.logging.mdc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class AddRequestIdToMappedDiagnosticContextTest {

  @Mock private HttpServletRequest mockHttpRequest;

  @Mock private HttpServletResponse mockHttpResponse;

  @Mock private FilterChain mockFilterChain;

  private AddRequestIdToMappedDiagnosticContext filter;

  @BeforeEach
  void beforeEach() {
    MDC.clear();
    filter = new AddRequestIdToMappedDiagnosticContext();
  }

  @AfterEach
  void afterEach() {
    MDC.clear();
  }

  @Test
  void should_PopulateTheRequestIdInTheMappedDiagnosticContext()
      throws ServletException, IOException {
    assertNull(getRequestIdFromTheMdc(), "Failed precondition");

    filter.doFilter(mockHttpRequest, mockHttpResponse, mockFilterChain);

    String actualRequestId = getRequestIdFromTheMdc();
    assertAll(
        () -> assertThat(actualRequestId, Matchers.not(Matchers.isEmptyOrNullString())),
        () -> assertThat(actualRequestId.length(), Matchers.greaterThanOrEqualTo(30)));
  }

  private String getRequestIdFromTheMdc() {
    return MDC.get(MappedDiagnosticContextKey.REQUEST_ID.getMdcKey());
  }
}
