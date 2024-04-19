package com.cgi.example.petstore.external.vaccinations;

import com.cgi.example.external.animalvaccination.model.Vaccination;
import com.cgi.example.petstore.model.PetStoreVaccination;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VaccinationsServiceTest {

    private static final String VACCINATION_ID = "AF54785412K";

    @Mock
    private ExternalVaccinationsMapper mapper;

    @Mock
    private VaccinationsApiClient apiClient;

    @InjectMocks
    private VaccinationsService service;

    @Test
    void when_ApiClientReturnsEmptyVaccinations_Should_ReturnEmptyList() {
        when(apiClient.getVaccinations(VACCINATION_ID)).thenReturn(Optional.empty());

        List<PetStoreVaccination> vaccinations = service.getVaccinationDetails(VACCINATION_ID);

        assertThat(vaccinations, Matchers.emptyIterable());
    }

    @Test
    void when_ApiClientReturnsVaccinations_Should_ReturnPopulatedList() {
        final List<Vaccination> externalVaccinations = List.of(new Vaccination(), new Vaccination());

        when(apiClient.getVaccinations(VACCINATION_ID)).thenReturn(Optional.of(externalVaccinations));
        when(mapper.mapToPetStoreVaccinations(externalVaccinations))
                .thenReturn(List.of(new PetStoreVaccination(), new PetStoreVaccination()));

        List<PetStoreVaccination> vaccinations = service.getVaccinationDetails(VACCINATION_ID);

        assertThat(vaccinations, Matchers.iterableWithSize(2));
    }
}