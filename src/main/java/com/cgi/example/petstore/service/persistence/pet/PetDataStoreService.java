package com.cgi.example.petstore.service.persistence.pet;

import com.cgi.example.petstore.exception.NotFoundException;
import com.cgi.example.petstore.model.NewPet;
import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.model.PetPatch;
import com.cgi.example.petstore.model.PetStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetDataStoreService {

    private final PetMapper petMapper;
    private final PetRepository petRepository;

    public Pet insertNewPet(NewPet newPet) {
        Pet pet = petMapper.mapToPet(newPet);
        PetDocument petDocument = petMapper.mapToPetDocument(pet);

        PetDocument insertedPetDocument = petRepository.insert(petDocument);

        Pet insertedPet = petMapper.mapToPet(insertedPetDocument);
        log.debug("Successfully inserted Pet with Id: [{}]", insertedPet.getPetId());
        return insertedPet;
    }

    public Pet findPetById(long id) {
        PetDocument petDocument = retrievePetDocument(id);
        return petMapper.mapToPet(petDocument);
    }

    private PetDocument retrievePetDocument(long petId) {
        Optional<PetDocument> petDocumentOptional = petRepository.findById(petId);
        if (petDocumentOptional.isEmpty()) {
            String message = "Unable to find the pet with Id: [%d]".formatted(petId);
            throw new NotFoundException(message);
        }

        return petDocumentOptional.get();
    }

    public List<Pet> findPetsByStatus(List<PetStatus> statuses) {
        List<String> petDocumentStatuses = petMapper.mapToPetStatusStrings(statuses);
        List<PetDocument> petDocumentsWithMatchingStatus = petRepository.findByPetStatusIn(petDocumentStatuses);

        return petMapper.mapToPets(petDocumentsWithMatchingStatus);
    }

    public Pet patch(PetPatch petPatch) {
        Pet petToBePatched = findPetById(petPatch.getId());

        petMapper.updateTargetObjectFromSourceObject(petPatch, petToBePatched);
        PetDocument petDocumentToSave = petMapper.mapToPetDocument(petToBePatched);

        PetDocument insertedPetDocument = petRepository.save(petDocumentToSave);

        return petMapper.mapToPet(insertedPetDocument);
    }

    public Optional<Long> findOwnerCustomerIdForPet(long petId) {
        Optional<PetDocument> optionalPetDocument = petRepository.findById(petId);
        if (optionalPetDocument.isEmpty()) {
            return Optional.empty();
        }

        PetDocument petDocument = optionalPetDocument.get();

        return Optional.ofNullable(petDocument.getOwnerCustomerId());
    }

    public Pet updatePetWithNewOwner(long petId, long customerId) {
        PetDocument petDocument = retrievePetDocument(petId);
        petDocument.setOwnerCustomerId(customerId);
        petDocument.setPetStatus(PetStatus.PENDING_COLLECTION.getValue());

        PetDocument savedPetDocument = petRepository.save(petDocument);
        return findPetById(savedPetDocument.getPetId());
    }
}
