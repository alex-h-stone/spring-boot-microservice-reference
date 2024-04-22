package com.cgi.example.petstore.config;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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
public class MappedDiagnosticFilterChainContextFilterTest {

  @Mock private HttpServletRequest httpRequest;

  @Mock private HttpServletResponse httpResponse;

  private MappedDiagnosticContextFilter filter;

  @BeforeEach
  void beforeEach() {
    MDC.clear();
    filter = new MappedDiagnosticContextFilter();
  }

  @AfterEach
  void afterEach() {
    MDC.clear();
  }

  @Test
  void should_PopulateMDCWithAuthenticatedUser() throws ServletException, IOException {
    String userId = "alex.stone@cgi.com";
    String remoteAddress = "192.168.1.1";

    when(httpRequest.getUserPrincipal()).thenReturn(() -> userId);
    when(httpRequest.getRemoteAddr()).thenReturn(remoteAddress);

    FilterChainWithAssertions filterChainWithAssertions =
        new FilterChainWithAssertions(
            () ->
                assertAll(
                    () -> assertEquals(userId, getMdcUserId()),
                    () -> assertEquals(remoteAddress, getMdcRemoteAddress())));

    filter.doFilter(httpRequest, httpResponse, filterChainWithAssertions);

    assertTrue(filterChainWithAssertions.wasExecuted());
    verifyMdcHasBeenClearedDown();
  }

  @Test
  void should_PopulateMDCWithUnauthenticatedUserAndUnknownRemoteAddress() throws Exception {
    when(httpRequest.getUserPrincipal()).thenReturn(null);
    when(httpRequest.getRemoteAddr()).thenReturn(null);

    FilterChainWithAssertions filterChainWithAssertions =
        new FilterChainWithAssertions(
            () ->
                assertAll(
                    () -> assertEquals("UnauthenticatedUser", getMdcUserId()),
                    () -> assertEquals("UnknownRemoteAddress", getMdcRemoteAddress())));

    filter.doFilter(httpRequest, httpResponse, filterChainWithAssertions);

    assertTrue(filterChainWithAssertions.wasExecuted());
    verifyMdcHasBeenClearedDown();
  }

  private void verifyMdcHasBeenClearedDown() {
    assertNull(getMdcUserId());
    assertNull(getMdcRemoteAddress());
  }

  private String getMdcRemoteAddress() {
    return MDC.get("remoteAddress");
  }

  private String getMdcUserId() {
    return MDC.get("userId");
  }

  private static final class FilterChainWithAssertions implements FilterChain {
    private final Runnable doFilterAssertions;
    private boolean wasExecuted;

    public FilterChainWithAssertions(Runnable doFilterAssertions) {
      this.doFilterAssertions = doFilterAssertions;
      this.wasExecuted = false;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) {
      doFilterAssertions.run();
      wasExecuted = true;
    }

    public boolean wasExecuted() {
      return wasExecuted;
    }
  }
}
