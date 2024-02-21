package com.cgi.example.petstore.external.vaccinations;

import com.cgi.example.external.animalvaccination.model.Vaccination;
import com.cgi.example.external.animalvaccination.model.VaccinationsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.util.UriBuilder;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
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

            // TODO add null check for null /empty body
            List<@Valid Vaccination> vaccinations = vaccinationsResponse.getBody().getVaccinations();
            log.debug("Retrieved {} vaccinations with URI: [{}]", vaccinations.size(), uri);
            return Optional.of(vaccinations);

        } catch (RuntimeException e) {
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

    private ResponseEntity<VaccinationsResponse> getVaccinationsResponse(URI uri) throws WebClientException {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(VaccinationsResponse.class)
                .retryWhen(Retry.backoff(2, Duration.of(200, ChronoUnit.MILLIS)))
                .block();
    }
}
