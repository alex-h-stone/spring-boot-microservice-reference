package com.cgi.example.petstore.utils;

import com.cgi.example.petstore.model.Address;
import com.cgi.example.petstore.model.CustomerRequest;
import com.cgi.example.petstore.model.NewPetRequest;
import com.cgi.example.petstore.model.PetAvailabilityStatus;
import com.cgi.example.petstore.model.PetInformationItem;
import com.cgi.example.petstore.model.PetResponse;
import com.cgi.example.petstore.model.PetType;
import com.cgi.example.petstore.service.customer.CustomerAddressPersistenceType;
import com.cgi.example.petstore.service.customer.CustomerDocument;
import com.cgi.example.petstore.service.pet.PersistenceStatus;
import com.cgi.example.petstore.service.pet.PetDocument;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Disabled;

@Disabled("Not a test class")
public class TestData {

  private static final String PET_ID = "KT1546";
  private static final String PET_NAME = "Fido";
  private static final PetType DOG_PET_TYPE = PetType.DOG;
  private static final String VACCINATION_ID = "AF54785412K";
  private static final List<String> PHOTO_URLS =
      List.of(
          "https://www.freepik.com/free-photo/isolated-happy-smiling-dog-white-background-portrait-4_39994000.htm#uuid=4f38a524-aa89-430d-8041-1de9ffb631c6");
  private static final String USERNAME = "alex.stone";
  private static final String FIRST_NAME = "Alex";
  private static final String LAST_NAME = "Stone";
  private static final String EMAIL = "alex.stone@cgi.com";
  private static final String STREET = "40 Princes Street";
  private static final String CITY = "Edinburgh";
  private static final String POST_CODE = "EH2 2BY";
  private static final String COUNTRY = "United Kingdom";

  public PetResponse createPetResponse() {
    PetResponse pet = new PetResponse();

    pet.setPetId(PET_ID);
    pet.setVaccinationId(VACCINATION_ID);
    pet.setVaccinations(Collections.emptyList());
    pet.setName(PET_NAME);
    pet.setPetType(DOG_PET_TYPE);
    pet.photoUrls(PHOTO_URLS);
    pet.setAvailabilityStatus(PetAvailabilityStatus.AVAILABLE_FOR_PURCHASE);
    pet.setAdditionalInformation(Collections.emptyList());

    return pet;
  }

  public PetDocument createPetDocument() {
    return createPetDocument(PET_ID);
  }

  public PetDocument createPetDocument(String petId) {
    LocalDateTime now = LocalDateTime.now();
    return PetDocument.builder()
        .petId(petId)
        .vaccinationId(VACCINATION_ID)
        .name(PET_NAME)
        .petType(DOG_PET_TYPE.getValue())
        .photoUrls(PHOTO_URLS)
        .additionalInformation(Collections.emptyList())
        .petStatus(PetAvailabilityStatus.AVAILABLE_FOR_PURCHASE.getValue())
        .persistenceStatus(PersistenceStatus.ACTIVE.getValue())
        .createdAt(now)
        .lastModified(now)
        .build();
  }

  public NewPetRequest createNewPetRequest() {
    NewPetRequest pet = new NewPetRequest();

    pet.setVaccinationId(VACCINATION_ID);
    pet.setName(PET_NAME);
    pet.setPetType(DOG_PET_TYPE);
    pet.photoUrls(PHOTO_URLS);
    pet.setAdditionalInformation(List.of(createPetInformationItem("Personality", "Energetic")));

    return pet;
  }

  public PetInformationItem createPetInformationItem(String name, String description) {
    PetInformationItem petInformationItem = new PetInformationItem();

    petInformationItem.setName(name);
    petInformationItem.setDescription(description);

    return petInformationItem;
  }

  public CustomerRequest createCustomerRequest() {
    CustomerRequest customer = new CustomerRequest();

    customer.setUsername(USERNAME);
    customer.setFirstName(FIRST_NAME);
    customer.setLastName(LAST_NAME);
    customer.email(EMAIL);
    customer.setAddress(createAddress());

    return customer;
  }

  private Address createAddress() {
    Address address = new Address();

    address.setStreet(STREET);
    address.setCity(CITY);
    address.setPostCode(POST_CODE);
    address.setCountry(COUNTRY);

    return address;
  }

  public CustomerDocument createCustomerDocument() {
    return CustomerDocument.builder()
        .customerId("6672aa8e398c316a8390fab0")
        .username(USERNAME)
        .firstName(FIRST_NAME)
        .lastName(LAST_NAME)
        .email(EMAIL)
        .address(createCustomerAddressPersistenceType())
        .build();
  }

  private CustomerAddressPersistenceType createCustomerAddressPersistenceType() {
    return CustomerAddressPersistenceType.builder()
        .street(STREET)
        .city(CITY)
        .postCode(POST_CODE)
        .country(COUNTRY)
        .build();
  }
}
