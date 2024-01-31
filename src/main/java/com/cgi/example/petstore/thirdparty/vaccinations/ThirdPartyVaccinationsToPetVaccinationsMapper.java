package com.cgi.example.petstore.thirdparty.vaccinations;

import com.cgi.example.thirdparty.animalvaccination.model.Vaccination;
import jakarta.validation.Valid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
interface ThirdPartyVaccinationsToPetVaccinationsMapper {

    @Mapping(target = "name", source = "vaccinationName")
    com.cgi.example.petstore.model.Vaccination map(@Valid Vaccination thirdPartyVaccination);

    List<com.cgi.example.petstore.model.Vaccination> map(List<@Valid Vaccination> thirdPartyVaccinations);
}
