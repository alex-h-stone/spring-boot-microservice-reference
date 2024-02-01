package com.cgi.example.petstore.integration;

import com.cgi.example.petstore.integration.utils.BaseIntegrationTest;
import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.service.persistence.DataStoreService;
import com.cgi.example.petstore.service.persistence.PetDocument;
import com.cgi.example.petstore.service.persistence.PetRepository;
import com.cgi.example.petstore.utils.TestData;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataStoreServiceIntegrationTest extends BaseIntegrationTest {

    private final TestData testData = new TestData();

    @Autowired
    private DataStoreService dataStoreService;

    @Autowired
    private PetRepository petRepository;

    @Test
    void shouldSavePetToMongoDB() {
        Pet petToSave = testData.createPet();
        String expectedPetId = String.valueOf(petToSave.getId());

        assertThat("Failed precondition", petRepository.findAll(), Matchers.empty());

        dataStoreService.save(petToSave);

        List<PetDocument> actualAllPetDocuments = petRepository.findAll();
        assertThat(actualAllPetDocuments, Matchers.iterableWithSize(1));
        PetDocument allPetDocument = actualAllPetDocuments.getFirst();
        assertEquals(expectedPetId, allPetDocument.getId());
    }
}
