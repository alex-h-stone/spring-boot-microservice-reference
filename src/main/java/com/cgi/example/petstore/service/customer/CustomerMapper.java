package com.cgi.example.petstore.service.customer;

import com.cgi.example.petstore.model.CustomerRequest;
import com.cgi.example.petstore.model.CustomerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    CustomerDocument mapToCustomerDocument(CustomerRequest customerToMap);

    /*    @Mapping(target =
                "vaccinations", ignore = true)*/
    CustomerResponse mapToCustomerResponse(CustomerDocument customerDocumentToMap);
}
