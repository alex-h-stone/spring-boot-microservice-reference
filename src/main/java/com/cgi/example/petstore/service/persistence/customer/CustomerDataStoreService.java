package com.cgi.example.petstore.service.persistence.customer;

import com.cgi.example.petstore.exception.NotFoundException;
import com.cgi.example.petstore.model.Customer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CustomerDataStoreService {

    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;

    public Customer insertIfAbsent(Customer customer) {
        Long customerId = customer.getCustomerId();

        Optional<CustomerDocument> optionalCustomerDocument = customerRepository.findById(customerId);
        if (optionalCustomerDocument.isPresent()) {
            return customerMapper.map(optionalCustomerDocument.get());
        }

        CustomerDocument customerDocument = customerMapper.map(customer);
        CustomerDocument savedCustomerDocument = customerRepository.insert(customerDocument);
        return customerMapper.map(savedCustomerDocument);
    }

    public Customer retrieveCustomer(long customerId) {
        Optional<CustomerDocument> optionalCustomerDocument = customerRepository.findById(customerId);
        if (optionalCustomerDocument.isEmpty()) {
            String message = "Unable to find the Customer with customerId: [%d]".formatted(customerId);
            throw new NotFoundException(message);
        }

        CustomerDocument customerDocument = optionalCustomerDocument.get();
        return customerMapper.map(customerDocument);
    }
}