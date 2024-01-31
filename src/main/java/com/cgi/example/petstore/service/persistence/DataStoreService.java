package com.cgi.example.petstore.service.persistence;

import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.model.PetStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public Optional<Pet> findPetById(String id) {
        Optional<PetDocument> petDocumentOptional = petRepository.findById(id);
        if (petDocumentOptional.isEmpty()) {
            return Optional.empty();
        }

        PetDocument petDocument = petDocumentOptional.get();
        Pet pet = petAndPetDocumentMapper.map(petDocument);
        return Optional.of(pet);
    }

    public List<Pet> findPetsByStatus(List<PetStatus> statuses) {
     /*   List<Pet> examples = statuses.stream().map(new Function<PetStatus, Pet>() {
            @Override
            public Pet apply(PetStatus petStatus) {
                Pet pet = new Pet();
                pet.setPetStatus(petStatus);
                return pet;
            }
        }).collect(Collectors.toList());


        ExampleMatcher.matchingAny();
        Example.of()*/

        List<PetDocument> byPetStatusIn = petRepository.findByPetStatusIn(statuses);
        List<Pet> map = petAndPetDocumentMapper.map(byPetStatusIn);
        return map;
    }
}
