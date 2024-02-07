package com.cgi.example.petstore.thirdparty.vaccinations;

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
    public UriBuilder vaccinationsUriBuilder(@Value("${thirdparty.apis.vaccinations.baseUrl}") String baseUrl,
                                             @Value("${thirdparty.apis.vaccinations.path}") String path) {
        return UriComponentsBuilder.newInstance()
                .uri(URI.create(baseUrl))
                .path(path);
    }
}
