package com.cgi.example.petstore.service;

import com.cgi.example.petstore.external.vaccinations.VaccinationsService;
import com.cgi.example.petstore.model.CustomerRequest;
import com.cgi.example.petstore.model.CustomerResponse;
import com.cgi.example.petstore.model.NewPet;
import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.model.PetPatch;
import com.cgi.example.petstore.model.PetStatus;
import com.cgi.example.petstore.model.Vaccination;
import com.cgi.example.petstore.service.customer.CustomerDataStoreService;
import com.cgi.example.petstore.service.pet.PetDataStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetService {

    private final VaccinationsService vaccinationsService;
    private final PetDataStoreService petDataStoreService;
    private final CustomerDataStoreService customerDataStoreService;

    public Pet addToPetStore(NewPet newPet) {
        Pet savedPet = petDataStoreService.insertNewPet(newPet);

        return enrichWithAdditionalInformation(savedPet);
    }

    public Pet retrievePetDetails(String petId) {
        Pet foundPet = petDataStoreService.findPetById(petId);

        return enrichWithAdditionalInformation(foundPet);
    }

    private Pet enrichWithAdditionalInformation(Pet pet) {
        List<Vaccination> vaccinations = vaccinationsService.getVaccinationDetails(pet.getVaccinationId());

        pet.setVaccinations(vaccinations);

        Optional<String> optionalCustomerId = petDataStoreService.findOwnerCustomerIdForPet(pet.getPetId());
        if (optionalCustomerId.isPresent()) {
            CustomerResponse customerResponse = customerDataStoreService.retrieveCustomer(optionalCustomerId.get());
            pet.setOwner(customerResponse);
        }
        return pet;
    }

    public List<Pet> retrieveAllPetsWithAStatusMatching(List<PetStatus> statuses) {
        List<Pet> petsMatchingStatus = petDataStoreService.findPetsByStatus(statuses);

        return petsMatchingStatus
                .stream()
                .map(this::enrichWithAdditionalInformation)
                .collect(Collectors.toList());
    }

    public Pet patch(PetPatch pet) {
        Pet patchedPet = petDataStoreService.patch(pet);
        log.debug("Successfully patched the pet with petId [{}]", patchedPet.getPetId());
        return retrievePetDetails(patchedPet.getPetId());
    }

    public Pet purchase(String petId, CustomerRequest customer) {
        CustomerResponse savedCustomer = customerDataStoreService.insertIfAbsent(customer);

        Pet purchasedPet = petDataStoreService.updatePetWithNewOwner(petId, savedCustomer.getCustomerId());

        return enrichWithAdditionalInformation(purchasedPet);
    }
}
