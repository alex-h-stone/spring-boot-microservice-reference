package com.cgi.example.petstore.service.persistence;

import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.model.PetStatus;
import com.cgi.example.petstore.model.PetType;
import com.cgi.example.petstore.service.persistence.pet.PetDocument;
import com.cgi.example.petstore.service.persistence.pet.PetMapper;
import com.cgi.example.petstore.utils.TestData;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PetMapperTest {

    private final TestData testData = new TestData();

    private final PetMapper mapper = Mappers.getMapper(PetMapper.class);

    @Test
    void shouldSuccessfullyMapFromAPetDocumentToPet() {
        Pet actualPet = mapper.mapToPet(testData.createPetDocument());

        assertNotNull(actualPet);
        assertAll(
                () -> assertNotNull(actualPet),
                () -> assertEquals(10, actualPet.getPetId()),
                () -> assertEquals("AF54785412K", actualPet.getVaccinationId()),
                () -> assertEquals("Fido", actualPet.getName()),
                () -> assertEquals(PetType.DOG, actualPet.getPetType()),
                () -> assertEquals(PetStatus.AVAILABLE_FOR_PURCHASE, actualPet.getPetStatus()),
                () -> assertThat(actualPet.getPhotoUrls(), Matchers.contains("https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6")),
                () -> assertThat(actualPet.getVaccinations(), CoreMatchers.nullValue()),
                () -> assertThat(actualPet.getAdditionalInformation(), Matchers.empty())
        );
    }

    @Test
    void shouldSuccessfullyMapFromAPetToPetDocument() {
        Pet pet = testData.createPet();

        PetDocument actualPetDocument = mapper.mapToPetDocument(pet);

        assertAll(
                () -> assertNotNull(actualPetDocument),
                () -> assertEquals(10, actualPetDocument.getPetId()),
                () -> assertEquals("AF54785412K", actualPetDocument.getVaccinationId()),
                () -> assertEquals("Fido", actualPetDocument.getName()),
                () -> assertEquals(PetType.DOG.name(), actualPetDocument.getPetType()),
                () -> assertEquals(PetStatus.AVAILABLE_FOR_PURCHASE.getValue(), actualPetDocument.getPetStatus()),
                () -> assertThat(actualPetDocument.getPhotoUrls(), Matchers.contains("https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6")),
                () -> assertThat(actualPetDocument.getAdditionalInformation(), Matchers.empty())
        );
    }

    @Test
    void shouldSuccessfullyMapFromANewPetToPet() {
        Pet actualPet = mapper.mapToPet(testData.createNewPet());

        assertNotNull(actualPet);
        assertAll(
                () -> assertNotNull(actualPet),
                () -> assertEquals(10, actualPet.getPetId()),
                () -> assertEquals("AF54785412K", actualPet.getVaccinationId()),
                () -> assertEquals("Fido", actualPet.getName()),
                () -> assertEquals(PetType.DOG, actualPet.getPetType()),
                () -> assertEquals(PetStatus.AVAILABLE_FOR_PURCHASE, actualPet.getPetStatus()),
                () -> assertThat(actualPet.getPhotoUrls(), Matchers.contains("https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6")),
                () -> assertThat(actualPet.getVaccinations(), CoreMatchers.nullValue()),
                () -> assertThat(actualPet.getAdditionalInformation(), Matchers.hasSize(1)),
                () -> assertThat(actualPet.getAdditionalInformation(), Matchers.contains(testData.createPetInformationItem("Personality", "Energetic")))
        );
    }
}