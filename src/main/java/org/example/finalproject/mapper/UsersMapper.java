package org.example.finalproject.mapper;

import org.example.finalproject.dto.EmailVerifyOtpDto;
import org.example.finalproject.dto.RegisterFinishDto;
import org.example.finalproject.dto.UsersUpdateFullNameRequestDto;
import org.example.finalproject.dto.UserResponseDto;
import org.example.finalproject.entity.Users;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UsersMapper {

    @Mapping(target = "password", ignore = true)
    Users toEntity(RegisterFinishDto dto);

    UserResponseDto toResponseDto(Users user);

    default UsersUpdateFullNameRequestDto toFullNameDto(Users users) {
        return UsersUpdateFullNameRequestDto.builder()
                .name(users.getName())
                .surname(users.getSurname())
                .build();
    }
}
