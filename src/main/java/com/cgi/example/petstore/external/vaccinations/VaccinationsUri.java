package com.cgi.example.petstore.external.vaccinations;

import java.net.URI;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(VaccinationsConfiguration.class)
public class VaccinationsUri {

  private final VaccinationsConfiguration vaccinationsConfiguration;

  public URI with(Map<String, ?> uriVariables) {
    return UriComponentsBuilder.newInstance()
        .uri(URI.create(vaccinationsConfiguration.getBaseUrl()))
        .path(vaccinationsConfiguration.getPath())
        .build(uriVariables);
  }
}
