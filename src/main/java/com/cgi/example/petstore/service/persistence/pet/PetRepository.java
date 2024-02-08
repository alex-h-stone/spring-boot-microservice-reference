package com.cgi.example.petstore.service.persistence.pet;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PetRepository extends MongoRepository<PetDocument, Long> {

    List<PetDocument> findByPetStatusIn(Collection<String> statuses);
}
