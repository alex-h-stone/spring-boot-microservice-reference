package com.cgi.example.petstore.thirdparty.vaccinations;

import com.cgi.example.thirdparty.animalvaccination.api.VaccinationsApi;
import com.cgi.example.thirdparty.animalvaccination.model.VaccinationsResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class VaccinationsApiClient implements VaccinationsApi {

    private final WebClient webClient;
    private final VaccinationsApiConfig config;

    @Override
    public ResponseEntity<VaccinationsResponse> getVaccinationDetails(String vaccinationId) {
        URI uri = UriComponentsBuilder
                .fromPath(config.getPath())
                .scheme(config.getScheme())
                .host(config.getHost())
                .port(config.getPort())
                .build(Map.of("vaccinationId", vaccinationId));

        try {
            log.info("About to getVaccinationDetails via the REST API call [{}]", uri.toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        ResponseEntity<VaccinationsResponse> vaccinationsResponse = webClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(VaccinationsResponse.class)
                .block();
        return vaccinationsResponse;
    }
}
