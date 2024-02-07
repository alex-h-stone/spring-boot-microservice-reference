package com.cgi.example.petstore.service.persistence;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Data
@Document(collection = "customers")
public class CustomerDocument {

    @Id
    @Indexed(unique = true)
    private Long customerId;

    @Indexed(unique = true)
    private String username;

    private String firstName;

    private String lastName;

    private String email;

    private CustomerAddressPersistenceType address;
}
