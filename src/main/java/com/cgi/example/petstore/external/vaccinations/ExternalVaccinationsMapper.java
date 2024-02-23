package com.cgi.example.petstore.external.vaccinations;

import com.cgi.example.external.animalvaccination.model.Vaccination;
import com.cgi.example.petstore.model.PetStoreVaccination;
import jakarta.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExternalVaccinationsMapper {

    public PetStoreVaccination mapToPetStoreVaccination(@Valid Vaccination externalVaccination) {
        PetStoreVaccination petStoreVaccination = new PetStoreVaccination();

        petStoreVaccination.setName(externalVaccination.getVaccinationName());
        petStoreVaccination.setDateOfAdminister(externalVaccination.getDateOfAdminister());

        return petStoreVaccination;
    }

    public List<PetStoreVaccination> mapToPetStoreVaccinationList(List<@Valid Vaccination> externalVaccinations) {
        return CollectionUtils.emptyIfNull(externalVaccinations)
                .stream()
                .map(this::mapToPetStoreVaccination)
                .collect(Collectors.toList());
    }
}
