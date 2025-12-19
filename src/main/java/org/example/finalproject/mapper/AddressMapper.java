package org.example.finalproject.mapper;

import org.example.finalproject.dto.AddressDto;
import org.example.finalproject.entity.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    default AddressDto toDto(Address address) {
        return AddressDto.builder()
                .city(address.getCity())
                .street(address.getStreet())
                .home(address.getHome())
                .build();
    }

    Address toEntity(AddressDto addressDto);

    AddressDto toResponseDto(Address address);
}
