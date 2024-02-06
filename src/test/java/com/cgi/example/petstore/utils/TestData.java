package com.cgi.example.petstore.utils;

import com.cgi.example.petstore.model.NewPet;
import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.model.PetStatus;
import com.cgi.example.petstore.model.PetType;
import com.cgi.example.petstore.service.persistence.PetDocument;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class TestData {

    private static final long TEN_PET_ID = 10L;
    private static final String FIDO_NAME = "Fido";
    private static final PetType DOG_PET_TYPE = PetType.DOG;
    private static final String VACCINATION_ID = "AF54785412K";
    private static final List<String> DEFAULT_PHOTO_URLS = List.of("https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6");

    public Pet createPet() {
        Pet pet = new Pet();

        pet.setId(TEN_PET_ID);
        pet.setVaccinationId(VACCINATION_ID);
        pet.setVaccinations(Collections.emptyList());
        pet.setName(FIDO_NAME);
        pet.setPetType(DOG_PET_TYPE);
        pet.photoUrls(DEFAULT_PHOTO_URLS);
        pet.setPetStatus(PetStatus.AVAILABLE_FOR_PURCHASE);
        pet.setAdditionalInformation(Collections.emptyList());

        return pet;
    }

    public PetDocument createPetDocument() {
        LocalDateTime now = LocalDateTime.now();
        return PetDocument.builder()
                .id(TEN_PET_ID)
                .vaccinationId(VACCINATION_ID)
                .name(FIDO_NAME)
                .petType(DOG_PET_TYPE.getValue())
                .photoUrls(DEFAULT_PHOTO_URLS)
                .additionalInformation(Collections.emptyList())
                .petStatus(PetStatus.AVAILABLE_FOR_PURCHASE.getValue())
                .createdAt(now)
                .lastModified(now)
                .build();
    }

    public NewPet createNewPet() {
        NewPet pet = new NewPet();

        pet.setId(TEN_PET_ID);
        pet.setVaccinationId(VACCINATION_ID);
        pet.setName(FIDO_NAME);
        pet.setPetType(DOG_PET_TYPE);
        pet.photoUrls(DEFAULT_PHOTO_URLS);
        pet.setAdditionalInformation(Collections.emptyList());

        return pet;
    }
}
