package org.example.finalproject.mapper;

import org.example.finalproject.dto.*;
import org.example.finalproject.entity.Users;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UsersMapper {

    @Mapping(target = "password", ignore = true)
    Users toEntity(RegisterFinishDto dto);

    @Mapping(target = "address", source = "addressDto")
    UserResponseDto toResponseDto(Users user, AddressDto addressDto);

    @Mapping(target = "address", source = "addressDto")
    UserResponsePublicDto toResponsePublicDto(Users user, AddressDto addressDto);

    default UsersUpdateFullNameRequestDto toFullNameDto(Users users) {
        return UsersUpdateFullNameRequestDto.builder()
                .name(users.getName())
                .surname(users.getSurname())
                .build();
    }
}
