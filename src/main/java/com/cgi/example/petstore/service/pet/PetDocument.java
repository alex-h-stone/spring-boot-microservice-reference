package com.cgi.example.petstore.service.pet;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@Document(collection = "pets")
public class PetDocument {

    @Id
    @Indexed(unique = true, name = "petIdIndex")
    private String petId;

    @Indexed(unique = false, name = "ownerCustomerIdIndex")
    private Long ownerCustomerId;

    private String vaccinationId;

    @Indexed(unique = false, name = "petNameIndex")
    private String name;

    private String petType;

    private List<String> photoUrls;

    private List<PetInformationItemPersistenceType> additionalInformation;

    @Indexed(unique = false, name = "petStatusIndex")
    private String petStatus;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModified;
}
