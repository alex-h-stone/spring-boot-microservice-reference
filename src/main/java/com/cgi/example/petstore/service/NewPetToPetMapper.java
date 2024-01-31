package com.cgi.example.petstore.service;

import com.cgi.example.petstore.model.NewPet;
import com.cgi.example.petstore.model.Pet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface NewPetToPetMapper {

    @Mapping(target = "vaccinations", ignore = true)
    @Mapping(target = "petStatus", ignore = true)
    Pet map(NewPet newPet);
}
