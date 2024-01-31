package com.cgi.example.petstore.thirdparty.vaccinations;

import com.cgi.example.petstore.model.Vaccination;
import com.cgi.example.thirdparty.animalvaccination.model.VaccinationsResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class VaccinationsService {

    private final ThirdPartyVaccinationsToPetVaccinationsMapper mapper;
    private final VaccinationsApiClient apiClient;

    public List<Vaccination> getVaccinationDetails(String vaccinationId) {
        ResponseEntity<VaccinationsResponse> vaccinationsResponse = apiClient.getVaccinationDetails(vaccinationId);

        @Valid List<com.cgi.example.thirdparty.animalvaccination.model.Vaccination> thirdPartyVaccinations = vaccinationsResponse
                .getBody()
                .getVaccinations();

        List<Vaccination> vaccinations = mapper.map(thirdPartyVaccinations);

        log.debug("Retrieved {} vaccinations for the vaccinationId [{}]",
                vaccinations.size(), vaccinationId);
        return vaccinations;
    }
}
