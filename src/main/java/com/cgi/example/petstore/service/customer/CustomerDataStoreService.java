package com.cgi.example.petstore.service.customer;

import com.cgi.example.petstore.exception.NotFoundExceptionAbstract;
import com.cgi.example.petstore.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerDataStoreService {

    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;

    public Customer insertIfAbsent(Customer customer) {
        Long customerId = customer.getCustomerId();

        Optional<CustomerDocument> optionalCustomerDocument = customerRepository.findById(customerId);
        if (optionalCustomerDocument.isPresent()) {
            return customerMapper.mapToCustomer(optionalCustomerDocument.get());
        }

        CustomerDocument customerDocument = customerMapper.mapToCustomer(customer);
        CustomerDocument savedCustomerDocument = customerRepository.insert(customerDocument);
        return customerMapper.mapToCustomer(savedCustomerDocument);
    }

    public Customer retrieveCustomer(long customerId) {
        Optional<CustomerDocument> optionalCustomerDocument = customerRepository.findById(customerId);
        if (optionalCustomerDocument.isEmpty()) {
            String message = "Unable to find the Customer with customerId: [%d]".formatted(customerId);
            throw new NotFoundExceptionAbstract(message);
        }

        CustomerDocument customerDocument = optionalCustomerDocument.get();
        return customerMapper.mapToCustomer(customerDocument);
    }
}
