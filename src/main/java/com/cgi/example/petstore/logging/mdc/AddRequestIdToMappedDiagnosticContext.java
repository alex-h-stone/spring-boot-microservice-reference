package com.cgi.example.petstore.logging.mdc;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AddRequestIdToMappedDiagnosticContext implements Filter {

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    String requestId = UUID.randomUUID().toString();
    MappedDiagnosticContextKey.REQUEST_ID.put(requestId);

    chain.doFilter(request, response);
  }
}
