package com.cgi.example.petstore.service.persistence;

import com.cgi.example.petstore.model.PetStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PetRepository extends MongoRepository<PetDocument, String> {

    List<PetDocument> findByPetStatusIn(Collection<PetStatus> statuses);
}
