package com.cgi.example.petstore.controller.validation;

import com.cgi.example.petstore.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PetValidator {

    private static final Long INVALID_ID = 666L;

    public void validatePetId(Long petId) {
        if (Objects.isNull(petId)) {
            String message = "Pet Id must not be null: %d".formatted(INVALID_ID);
            throw new ValidationException(message);
        }

        if (INVALID_ID.equals(petId)) {
            String message = "Invalid Pet Id, the Id %d is not permitted, found: %d"
                    .formatted(INVALID_ID, petId);
            throw new ValidationException(message);
        }
    }
}
