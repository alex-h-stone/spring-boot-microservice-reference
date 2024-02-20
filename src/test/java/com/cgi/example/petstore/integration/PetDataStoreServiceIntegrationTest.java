package com.cgi.example.petstore.integration;

import com.cgi.example.petstore.model.NewPet;
import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.service.pet.PetDataStoreService;
import com.cgi.example.petstore.service.pet.PetDocument;
import com.cgi.example.petstore.service.pet.PetRepository;
import com.cgi.example.petstore.utils.TestData;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PetDataStoreServiceIntegrationTest extends BaseIntegrationTest {

    private final TestData testData = new TestData();

    @Autowired
    private PetDataStoreService petDataStoreService;

    @Autowired
    private PetRepository petRepository;

    @Test
    void shouldSavePetToMongoDB() {
        NewPet petToSave = testData.createNewPet();

        assertThat("Failed precondition", petRepository.findAll(), Matchers.empty());

        Pet insertedPet = petDataStoreService.insertNewPet(petToSave);

        List<PetDocument> actualAllPetDocuments = petRepository.findAll();
        assertThat(actualAllPetDocuments, Matchers.iterableWithSize(1));
        PetDocument actualPetDocument = actualAllPetDocuments.getFirst();
        assertEquals(insertedPet.getPetId(), actualPetDocument.getPetId());
    }
}
