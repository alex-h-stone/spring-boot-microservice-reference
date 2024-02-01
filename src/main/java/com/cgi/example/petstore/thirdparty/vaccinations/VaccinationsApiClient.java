package com.cgi.example.petstore.thirdparty.vaccinations;

import com.cgi.example.thirdparty.animalvaccination.model.Vaccination;
import com.cgi.example.thirdparty.animalvaccination.model.VaccinationsResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class VaccinationsApiClient {

    private final WebClient webClient;
    private final VaccinationsApiConfig config;

    public Optional<List<Vaccination>> getVaccinations(String vaccinationId) {
        URI uri = UriComponentsBuilder
                .fromPath(config.getPath())
                .scheme(config.getScheme())
                .host(config.getHost())
                .port(config.getPort())
                .build(Map.of("vaccinationId", vaccinationId));

        log.debug("About to get vaccinations for URI: [{}]", uri);

        try {
            ResponseEntity<VaccinationsResponse> vaccinationsResponse = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .toEntity(VaccinationsResponse.class)
                    .block();

            if (Objects.isNull(vaccinationsResponse) ||
                    !vaccinationsResponse.getStatusCode().is2xxSuccessful() ||
                    Objects.isNull(vaccinationsResponse.getBody()) ||
                    Objects.isNull(vaccinationsResponse.getBody().getVaccinations())) {
                log.info("Unable to determine vaccinations for vaccinationId: [{}]", vaccinationId);
                return Optional.empty();
            }

            List<@Valid Vaccination> vaccinations = vaccinationsResponse.getBody().getVaccinations();
            log.debug("Retrieved {} vaccinations for URI: [{}]", vaccinations.size(), uri);
            return Optional.of(vaccinations);

        } catch (WebClientResponseException e) {
            log.info("Unable to determine the vaccinations for vaccinationId: [{}] due to error [{}]",
                    vaccinationId, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
