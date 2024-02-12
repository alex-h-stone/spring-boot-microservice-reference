package com.cgi.example.petstore.thirdparty.vaccinations;

import com.cgi.example.thirdparty.animalvaccination.model.Vaccination;
import com.cgi.example.thirdparty.animalvaccination.model.VaccinationsResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

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
    private final UriBuilder vaccinationsUriBuilder;

    public Optional<List<Vaccination>> getVaccinations(String vaccinationId) {
        URI uri = vaccinationsUriBuilder.build(Map.of("vaccinationId", vaccinationId));

        log.debug("About to get vaccinations with URI: [{}]", uri);

        try {
            ResponseEntity<VaccinationsResponse> vaccinationsResponse = getVaccinationsResponse(uri);

            if (invalid(vaccinationsResponse)) {
                log.info("Unable to determine vaccinations for vaccinationId: [{}]", vaccinationId);
                return Optional.empty();
            }

            List<@Valid Vaccination> vaccinations = vaccinationsResponse.getBody().getVaccinations();
            log.debug("Retrieved {} vaccinations with URI: [{}]", vaccinations.size(), uri);
            return Optional.of(vaccinations);

        } catch (WebClientResponseException e) {
            log.info("Unable to determine the vaccinations for vaccinationId: [{}] due to error [{}]",
                    vaccinationId, e.getMessage(), e);
            return Optional.empty();
        }
    }

    private boolean invalid(ResponseEntity<VaccinationsResponse> vaccinationsResponse) {
        return Objects.isNull(vaccinationsResponse) ||
                !vaccinationsResponse.getStatusCode().is2xxSuccessful() ||
                Objects.isNull(vaccinationsResponse.getBody()) ||
                Objects.isNull(vaccinationsResponse.getBody().getVaccinations());
    }

    @Retryable(retryFor = {Exception.class}, maxAttempts = 2, backoff = @Backoff(delay = 1_000))
    private ResponseEntity<VaccinationsResponse> getVaccinationsResponse(URI uri) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(VaccinationsResponse.class)
                .block();
    }
}
