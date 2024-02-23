package com.cgi.example.petstore.service.persistence;

import com.cgi.example.petstore.model.NewPetRequest;
import com.cgi.example.petstore.service.pet.PetDataStoreService;
import com.cgi.example.petstore.service.pet.PetDocument;
import com.cgi.example.petstore.service.pet.PetMapper;
import com.cgi.example.petstore.service.pet.PetRepository;
import com.cgi.example.petstore.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetDataStoreServiceTest {

    private final TestData testData = new TestData();

    @Mock
    private PetRepository mockPetRepository;

    private PetDataStoreService petDataStoreService;

    @BeforeEach
    void setUp() {
        PetMapper mapper = new PetMapper();
        petDataStoreService = new PetDataStoreService(mapper, mockPetRepository);
    }

    @Test
    void shouldSaveSuccessfully() {
        NewPetRequest petToSave = testData.createNewPetRequest();
        when(mockPetRepository.insert(any(PetDocument.class)))
                .thenReturn(testData.createPetDocument());

        petDataStoreService.insertNewPet(petToSave);
        verify(mockPetRepository).insert(any(PetDocument.class));
    }
}