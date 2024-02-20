package com.cgi.example.petstore.controller;

import com.cgi.example.petstore.api.PetStoreApi;
import com.cgi.example.petstore.controller.validation.PetValidator;
import com.cgi.example.petstore.model.CustomerRequest;
import com.cgi.example.petstore.model.MultiplePetsResponse;
import com.cgi.example.petstore.model.NewPet;
import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.model.PetPatch;
import com.cgi.example.petstore.model.PetStatus;
import com.cgi.example.petstore.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PetStoreController implements PetStoreApi {

    private final PetValidator petValidator;
    private final PetService petService;

    @Override
    public ResponseEntity<Pet> addPet(NewPet newPet) {
        Pet addedPet = petService.addToPetStore(newPet);

        return ResponseEntity.ok(addedPet);
    }

    @Override
    public ResponseEntity<MultiplePetsResponse> findPetsByStatus(List<PetStatus> statuses) {
        List<Pet> pets = petService.retrieveAllPetsWithAStatusMatching(statuses);

        MultiplePetsResponse petsResponse = new MultiplePetsResponse();
        petsResponse.setPets(pets);

        return ResponseEntity.ok(petsResponse);
    }

    @Override
    public ResponseEntity<Pet> getPetById(String petId) {
        petValidator.validatePetId(petId);

        Pet pet = petService.retrievePetDetails(petId);

        return ResponseEntity.ok().body(pet);
    }

    @Override
    public ResponseEntity<Pet> purchasePet(String petId, CustomerRequest customer) {
        petValidator.validatePetId(petId);

        Pet purchasedPet = petService.purchase(petId, customer);

        return ResponseEntity.ok().body(purchasedPet);
    }

    @Override
    public ResponseEntity<Pet> patchPet(PetPatch petPatch) {
        Pet patchedPet = petService.patch(petPatch);

        return ResponseEntity.ok().body(patchedPet);
    }
}
