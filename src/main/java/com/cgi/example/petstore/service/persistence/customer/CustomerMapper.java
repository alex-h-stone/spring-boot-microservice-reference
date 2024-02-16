package com.cgi.example.petstore.service.persistence.customer;

import com.cgi.example.petstore.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    CustomerDocument mapToCustomer(Customer customerToMap);

    /*    @Mapping(target =
                "vaccinations", ignore = true)*/
    Customer mapToCustomer(CustomerDocument customerDocumentToMap);
}
