package com.cgi.example.petstore.external.vaccinations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
@Scope("prototype")
public class VaccinationsUriBuilderConfig {

    @Bean("vaccinationsUriBuilder")
    @Scope("prototype")
    // Design decision: This is a prototype as a UriBuilder is mutable and should not share state across threads or individual requests.
    public UriBuilder vaccinationsUriBuilder(@Value("${external.apis.vaccinations.baseUrl}") String baseUrl,
                                             @Value("${external.apis.vaccinations.path}") String path) {
        return UriComponentsBuilder.newInstance()
                .uri(URI.create(baseUrl))
                .path(path);
    }
}
