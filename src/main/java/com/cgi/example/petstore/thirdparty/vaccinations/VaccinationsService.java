package com.cgi.example.petstore.thirdparty.vaccinations;

import com.cgi.example.petstore.model.Vaccination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VaccinationsService {

    private final ThirdPartyVaccinationsToPetVaccinationsMapper mapper;
    private final VaccinationsApiClient apiClient;

    public List<Vaccination> getVaccinationDetails(String vaccinationId) {
        Optional<List<com.cgi.example.thirdparty.animalvaccination.model.Vaccination>> vaccinationsOptional =
                apiClient.getVaccinations(vaccinationId);

        if (vaccinationsOptional.isEmpty()) {
            return Collections.emptyList();
        }

        return mapper.map(vaccinationsOptional.get());
    }
}
