package com.cgi.example.petstore.service.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRepository extends MongoRepository<CustomerDocument, Long> {
}
