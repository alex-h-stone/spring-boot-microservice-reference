package com.cgi.example.petstore.service.persistence.pet;

import com.cgi.example.petstore.model.NewPet;
import com.cgi.example.petstore.model.Pet;
import com.cgi.example.petstore.model.PetPatch;
import com.cgi.example.petstore.model.PetStatus;
import com.cgi.example.petstore.model.PetType;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface PetMapper {

    @Mapping(target = "vaccinations", ignore = true)
    @Mapping(target = "petStatus", constant = "AVAILABLE_FOR_PURCHASE")
    Pet mapToPet(NewPet newPetToMap);

    // TODO tidy up mappings
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTargetObjectFromSourceObject(PetPatch patchToApply, @MappingTarget Pet targetPet);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    PetDocument mapToPetDocument(Pet petToMap);

    @Mapping(target = "vaccinations", ignore = true)
    Pet mapToPet(PetDocument petDocumentToMap);

    List<Pet> mapToPets(List<PetDocument> petDocuments);

    List<String> mapToPetStatusStrings(List<PetStatus> petStatuses);

    default String mapToString(PetStatus petStatus) {
        if (Objects.isNull(petStatus)) {
            return null;
        }
        return petStatus.getValue();
    }

    default PetType mapToPetType(String petTypeString) {
        if (Objects.isNull(petTypeString)) {
            return null;
        }
        return EnumUtils.getEnumIgnoreCase(PetType.class,
                petTypeString.replace(StringUtils.SPACE, "_"));
    }

    default PetStatus mapToPetStatus(String petStatusString) {
        if (Objects.isNull(petStatusString)) {
            return null;
        }
        return EnumUtils.getEnumIgnoreCase(PetStatus.class,
                petStatusString.replace(StringUtils.SPACE, "_"));
    }
}
