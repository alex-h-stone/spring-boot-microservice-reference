package com.cgi.example.petstore.service.persistence;

import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.utils.AssertionUtils;
import com.cgi.example.petstore.utils.TestData;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PetAndPetDocumentMapperTest {

    private final TestData testData = new TestData();
    private final AssertionUtils assertionUtils = new AssertionUtils();

    private final PetAndPetDocumentMapper mapper = Mappers.getMapper(PetAndPetDocumentMapper.class);

    @Test
    void shouldSuccessfullyMapFromAPetDocumentToPet() {
        Pet pet = mapper.map(testData.createPetDocument());

        String actualPetToString = pet.toString();
        assertNotNull(actualPetToString);
        assertionUtils.assertEqualsWithNormalisedSpaces("""
                class Pet {
                  id: 10
                  vaccinationId: AF54785412K
                  vaccinations: null
                  name: Fido
                  petType: Dog
                  photoUrls: []
                  additionalInformation: []
                  petStatus: Available For Purchase
                }
                """, actualPetToString);
    }

    @Test
    void shouldSuccessfullyMapFromAPetToPetDocument() {
        PetDocument petDocument = mapper.map(testData.createPet());

        String actualPetDocumentToString = petDocument.toString();
        assertNotNull(actualPetDocumentToString);
        assertionUtils.assertEqualsWithNormalisedSpaces("""
                PetDocument(id=10,
                  vaccinationId=AF54785412K,
                  name=Fido,
                  petType=DOG,
                  photoUrls=[],
                  additionalInformation=[],
                  petStatus=AVAILABLE_FOR_PURCHASE,
                  createdAt=null,
                  lastModified=null)
                  """, actualPetDocumentToString);
    }
}