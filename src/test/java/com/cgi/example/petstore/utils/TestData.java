package com.cgi.example.petstore.utils;

import com.cgi.example.petstore.model.NewPet;
import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.model.PetStatus;
import com.cgi.example.petstore.model.PetType;
import com.cgi.example.petstore.service.persistence.PetDocument;

import java.time.LocalDateTime;
import java.util.Collections;

public class TestData {

    private static final long ID = 10L;
    private static final String NAME = "Fido";
    private static final PetType PET_TYPE = PetType.DOG;
    private static final String VACCINATION_ID = "AF54785412K";

    public Pet createPet() {
        Pet pet = new Pet();

        pet.setId(ID);
        pet.setVaccinationId(VACCINATION_ID);
        pet.setVaccinations(Collections.emptyList());
        pet.setName(NAME);
        pet.setPetType(PET_TYPE);
        pet.photoUrls(Collections.emptyList());
        pet.setPetStatus(PetStatus.AVAILABLE_FOR_PURCHASE);
        pet.setAdditionalInformation(Collections.emptyList());

        return pet;
    }

    public PetDocument createPetDocument() {
        LocalDateTime now = LocalDateTime.now();
        return PetDocument.builder()
                .id(String.valueOf(ID))
                .vaccinationId(VACCINATION_ID)
                .name(NAME)
                .petType(PET_TYPE.getValue())
                .photoUrls(Collections.emptyList())
                .additionalInformation(Collections.emptyList())
                .petStatus(PetStatus.AVAILABLE_FOR_PURCHASE.getValue())
                .createdAt(now)
                .lastModified(now)
                .build();
    }

    public NewPet createNewPet() {
        NewPet pet = new NewPet();

        pet.setId(ID);
        pet.setVaccinationId(VACCINATION_ID);
        pet.setName(NAME);
        pet.setPetType(PET_TYPE);
        pet.photoUrls(Collections.emptyList());
        pet.setAdditionalInformation(Collections.emptyList());

        return pet;
    }
}
