package com.cgi.example.petstore.service.persistence.customer;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CustomerAddressPersistenceType {

    private String street;

    private String city;

    private String postCode;

    private String country;
}
