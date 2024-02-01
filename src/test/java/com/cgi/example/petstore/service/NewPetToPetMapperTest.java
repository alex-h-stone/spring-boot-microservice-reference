package com.cgi.example.petstore.service;

import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.utils.AssertionUtils;
import com.cgi.example.petstore.utils.TestData;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class NewPetToPetMapperTest {

    private final TestData testData = new TestData();
    private final AssertionUtils assertionUtils = new AssertionUtils();

    private final NewPetToPetMapper mapper = Mappers.getMapper(NewPetToPetMapper.class);

    @Test
    void shouldSuccessfullyMapFromANewPetToPet() {
        Pet pet = mapper.map(testData.createNewPet());

        String actualPetToString = pet.toString();
        assertNotNull(actualPetToString);
        assertionUtils.assertEqualsWithNormalisedSpaces("""
                class Pet {
                  id: 10
                  vaccinationId: AF54785412K
                  vaccinations: null
                  name: Fido
                  petType: Dog
                  photoUrls: [https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6]
                  additionalInformation: []
                  petStatus: Available For Purchase }
                """, actualPetToString);
    }
}