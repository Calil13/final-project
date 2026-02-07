package org.example.finalproject.mapper;

import org.example.finalproject.dto.AddressDto;
import org.example.finalproject.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressDto toDto(Address address);

    Address toEntity(AddressDto addressDto);

    AddressDto toResponseDto(Address address);
}
