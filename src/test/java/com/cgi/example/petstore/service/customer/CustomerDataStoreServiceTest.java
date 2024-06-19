package com.cgi.example.petstore.service.customer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cgi.example.petstore.exception.NotFoundException;
import com.cgi.example.petstore.model.CustomerRequest;
import com.cgi.example.petstore.model.CustomerResponse;
import com.cgi.example.petstore.utils.TestData;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
public class CustomerDataStoreServiceTest {

  @Mock private CustomerRepository customerRepositoryMock;

  private TestData testData;
  private CustomerDocument customerDocument;

  private CustomerDataStoreService customerDataStoreService;

  @BeforeEach
  void setUp() {
    testData = new TestData();
    customerDocument = testData.createCustomerDocument();

    customerDataStoreService =
        new CustomerDataStoreService(new CustomerMapper(), customerRepositoryMock);
  }

  @Test
  void testInsertIfAbsentWhenCustomerExists() {
    final String customerId = customerDocument.getCustomerId();
    CustomerRequest customerRequest = testData.createCustomerRequest();
    customerRequest.setCustomerId(customerId);

    when(customerRepositoryMock.findById(customerId)).thenReturn(Optional.of(customerDocument));

    CustomerResponse customerResponse = customerDataStoreService.insertIfAbsent(customerRequest);

    assertAll(
        () -> assertNotNull(customerResponse),
        () -> assertEquals(customerId, customerResponse.getCustomerId()),
        () -> verify(customerRepositoryMock).findById(customerId));
  }

  @Test
  void testInsertIfAbsentWhenCustomerDoesNotExist() {
    final String customerId = customerDocument.getCustomerId();
    CustomerRequest customerRequest = testData.createCustomerRequest();

    when(customerRepositoryMock.insert(any(CustomerDocument.class))).thenReturn(customerDocument);

    CustomerResponse customerResponse = customerDataStoreService.insertIfAbsent(customerRequest);

    assertAll(
        () -> assertNotNull(customerResponse),
        () -> assertEquals(customerId, customerResponse.getCustomerId()),
        () -> verify(customerRepositoryMock).insert(any(CustomerDocument.class)));
  }

  @Test
  void testRetrieveCustomerWhenCustomerExists() {
    final String customerId = customerDocument.getCustomerId();
    when(customerRepositoryMock.findById(customerId)).thenReturn(Optional.of(customerDocument));

    CustomerResponse customerResponse = customerDataStoreService.retrieveCustomer(customerId);

    assertAll(
        () -> assertNotNull(customerResponse),
        () -> assertEquals(customerId, customerResponse.getCustomerId()));
  }

  @Test
  void testRetrieveCustomerWhenCustomerDoesNotExist() {
    final String customerId = customerDocument.getCustomerId();
    when(customerRepositoryMock.findById(customerId)).thenReturn(Optional.empty());

    NotFoundException expectedException =
        assertThrows(
            NotFoundException.class, () -> customerDataStoreService.retrieveCustomer(customerId));

    assertAll(
        () ->
            assertEquals(
                "Unable to find the Customer with customerId: [%s]".formatted(customerId),
                expectedException.getMessage()),
        () -> verify(customerRepositoryMock).findById(customerId));
  }
}
