package com.cgi.example.petstore.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;
import java.util.Objects;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@WebFilter("/api/v1/*")
@Component
public class MappedDiagnosticContextFilter implements Filter {

  private static final String USER_ID = "userId";
  private static final String UNAUTHENTICATED_USER = "UnauthenticatedUser";

  private static final String REMOTE_ADDRESS = "remoteAddress";
  private static final String UNKNOWN_REMOTE_ADDRESS = "UnknownRemoteAddress";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {

    try {
      MDC.put(USER_ID, determineUserIdFromRequest(request));
      MDC.put(REMOTE_ADDRESS, determineRemoteAddress(request));

      filterChain.doFilter(request, response);
    } finally {
      MDC.remove(REMOTE_ADDRESS);
      MDC.remove(USER_ID);
    }
  }

  private String determineRemoteAddress(ServletRequest request) {
    return Objects.isNull(request.getRemoteAddr())
        ? UNKNOWN_REMOTE_ADDRESS
        : request.getRemoteAddr();
  }

  private String determineUserIdFromRequest(ServletRequest request) {
    if (request instanceof HttpServletRequest httpRequest) {
      Principal userPrincipal = httpRequest.getUserPrincipal();
      if (Objects.nonNull(userPrincipal)) {
        return userPrincipal.getName();
      }
    }
    return UNAUTHENTICATED_USER;
  }
}
