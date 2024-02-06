package com.cgi.example.petstore.service.persistence;

import com.cgi.example.petstore.exception.NotFoundException;
import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.model.PetPatch;
import com.cgi.example.petstore.model.PetStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class DataStoreService {

    private final PetAndPetDocumentMapper petAndPetDocumentMapper;
    private final PetRepository petRepository;

    public void save(Pet pet) {
        PetDocument petDocument = petAndPetDocumentMapper.map(pet);

        PetDocument insertedPet = petRepository.insert(petDocument);

        log.debug("Successfully saved Pet with Id: [{}]", insertedPet.getId());
    }

    public Optional<Pet> findPetById(Long id) {
        Optional<PetDocument> petDocumentOptional = petRepository.findById(id);
        if (petDocumentOptional.isEmpty()) {
            return Optional.empty();
        }

        PetDocument petDocument = petDocumentOptional.get();
        Pet pet = petAndPetDocumentMapper.map(petDocument);
        return Optional.of(pet);
    }

    public List<Pet> findPetsByStatus(List<PetStatus> statuses) {
        List<String> petDocumentStatuses = petAndPetDocumentMapper.mapPetStatuses(statuses);

        List<PetDocument> petDocumentsWithMatchingStatus = petRepository.findByPetStatusIn(petDocumentStatuses);

        return petAndPetDocumentMapper.mapPetDocuments(petDocumentsWithMatchingStatus);
    }

    public Pet patch(PetPatch petPatch) {
        Long petId = petPatch.getId();
        Optional<PetDocument> petDocument = petRepository.findById(petId);

        if (petDocument.isEmpty()) {
            String message = "Unable to patch as cannot find the pet with Id: [%d]".formatted(petId);
            throw new NotFoundException(message);
        }
        PetDocument petToPatch = petDocument.get();
        // TODO patch petToPatch with petPatch

        PetDocument patchedAndSavedPet = petRepository.save(petToPatch);

        return petAndPetDocumentMapper.map(patchedAndSavedPet);
    }
}
