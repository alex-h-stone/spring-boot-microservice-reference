package com.cgi.example.petstore.service;

import com.cgi.example.petstore.model.Customer;
import com.cgi.example.petstore.model.NewPet;
import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.model.PetPatch;
import com.cgi.example.petstore.model.PetStatus;
import com.cgi.example.petstore.model.Vaccination;
import com.cgi.example.petstore.service.persistence.customer.CustomerDataStoreService;
import com.cgi.example.petstore.service.persistence.pet.PetDataStoreService;
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
    private final PetDataStoreService petDataStoreService;
    private final CustomerDataStoreService customerDataStoreService;

    public Pet addToPetStore(NewPet newPet) {
        Pet savedPet = petDataStoreService.insertNewPet(newPet);

        return retrievePetDetails(savedPet.getPetId());
    }

    public Pet retrievePetDetails(long petId) {
        Pet pet = petDataStoreService.findPetById(petId);
        List<Vaccination> vaccinations = vaccinationsService.getVaccinationDetails(pet.getVaccinationId());

        pet.setVaccinations(vaccinations);

        Optional<Long> optionalCustomerId = petDataStoreService.findOwnerCustomerIdForPet(petId);
        if (optionalCustomerId.isPresent()) {
            Customer customer = customerDataStoreService.retrieveCustomer(optionalCustomerId.get());
            pet.setOwner(customer);
        }

        return pet;
    }

    public List<Pet> retrieveAllPetsWithAStatusMatching(List<PetStatus> statuses) {
        return petDataStoreService.findPetsByStatus(statuses);
    }

    public Pet patch(PetPatch pet) {
        Pet patchedPet = petDataStoreService.patch(pet);
        log.debug("Successfully patched the pet with petId [{}]", patchedPet.getPetId());
        return retrievePetDetails(patchedPet.getPetId());
    }

    public Pet purchase(long petId, Customer customer) {
        Customer savedCustomer = customerDataStoreService.insertIfAbsent(customer);

        Pet purchasedPet = petDataStoreService.updatePetWithNewOwner(petId, savedCustomer.getCustomerId());

        return retrievePetDetails(purchasedPet.getPetId());
    }
}
