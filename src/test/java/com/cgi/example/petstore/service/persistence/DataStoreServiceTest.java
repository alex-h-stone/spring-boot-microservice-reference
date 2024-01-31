package com.cgi.example.petstore.service.persistence;

import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.utils.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataStoreServiceTest {

    private final TestData testData = new TestData();

    @Mock
    private PetRepository mockPetRepository;

    private DataStoreService dataStoreService;

    @BeforeEach
    void setUp() {
        PetAndPetDocumentMapper mapper = Mappers.getMapper(PetAndPetDocumentMapper.class);
        dataStoreService = new DataStoreService(mapper, mockPetRepository);
    }

    @Test
    void shouldSaveSuccessfully() {
        Pet petToSave = testData.createPet();
        when(mockPetRepository.insert(any(PetDocument.class)))
                .thenReturn(testData.createPetDocument());

        dataStoreService.save(petToSave);

        verify(mockPetRepository).insert(any(PetDocument.class));
    }
}