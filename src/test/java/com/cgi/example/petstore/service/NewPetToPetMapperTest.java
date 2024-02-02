package com.cgi.example.petstore.service;

import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.model.PetStatus;
import com.cgi.example.petstore.model.PetType;
import com.cgi.example.petstore.utils.TestData;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NewPetToPetMapperTest {

    private final TestData testData = new TestData();

    private final NewPetToPetMapper mapper = Mappers.getMapper(NewPetToPetMapper.class);

    @Test
    void shouldSuccessfullyMapFromANewPetToPet() {
        Pet actualPet = mapper.map(testData.createNewPet());

        assertNotNull(actualPet);
        assertAll(
                () -> assertNotNull(actualPet),
                () -> assertEquals(10, actualPet.getId()),
                () -> assertEquals("AF54785412K", actualPet.getVaccinationId()),
                () -> assertEquals("Fido", actualPet.getName()),
                () -> assertEquals(PetType.DOG, actualPet.getPetType()),
                () -> assertEquals(PetStatus.AVAILABLE_FOR_PURCHASE, actualPet.getPetStatus()),
                () -> assertThat(actualPet.getPhotoUrls(), Matchers.contains("https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6")),
                () -> assertThat(actualPet.getVaccinations(), CoreMatchers.nullValue()),
                () -> assertThat(actualPet.getAdditionalInformation(), Matchers.empty())
        );
    }
}