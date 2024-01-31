package com.cgi.example.petstore.controller.validation;

import com.cgi.example.petstore.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class PetValidator {

    private static final Long INVALID_ID = 666L;

    public void validatePetId(Long petId) {

        if (INVALID_ID.equals(petId)) {
            String message = "Invalid Pet ID: %d".formatted(INVALID_ID);
            throw new ValidationException(message);
        }
    }
}
