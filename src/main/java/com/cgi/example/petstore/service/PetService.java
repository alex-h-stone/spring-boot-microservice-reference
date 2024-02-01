package com.cgi.example.petstore.service;

import com.cgi.example.petstore.exception.NotFoundException;
import com.cgi.example.petstore.model.NewPet;
import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.model.PetStatus;
import com.cgi.example.petstore.model.Vaccination;
import com.cgi.example.petstore.service.persistence.DataStoreService;
import com.cgi.example.petstore.thirdparty.vaccinations.VaccinationsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class PetService {

    private final VaccinationsService vaccinationsService;
    private final NewPetToPetMapper newPetToPetMapper;
    private final DataStoreService dataStoreService;

    public Pet addToPetStore(NewPet newPet) {
        List<Vaccination> vaccinations = vaccinationsService.getVaccinationDetails(newPet.getVaccinationId());

        Pet pet = newPetToPetMapper.map(newPet);
        pet.setVaccinations(vaccinations);

        dataStoreService.save(pet);

        return pet;
    }

    public Pet retrievePetDetails(long petId) {
        Optional<Pet> optionalPet = dataStoreService.findPetById(String.valueOf(petId));

        if (optionalPet.isEmpty()) {
            String message = "Unable to find the pet with Id: [%d]".formatted(petId);
            throw new NotFoundException(message);
        }

        return optionalPet.get();
    }

    public List<Pet> retrieveAllPetsWithAStatusMatching(List<PetStatus> statuses) {
        return dataStoreService.findPetsByStatus(statuses);
    }
}
