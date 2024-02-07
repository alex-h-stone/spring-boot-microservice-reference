package com.cgi.example.petstore.service.persistence;

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
    @Indexed(unique = true)
    private Long id;

    private String vaccinationId;

    @Indexed
    private String name;

    private String petType;

    private List<String> photoUrls;

    private List<PetInformationItemPersistenceType> additionalInformation;

    @Indexed
    private String petStatus;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModified;
}
