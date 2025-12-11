package org.example.finalproject.mapper;

import org.example.finalproject.dto.RegisterFinishDto;
import org.example.finalproject.dto.UpdateFullNameRequest;
import org.example.finalproject.dto.UserResponseDto;
import org.example.finalproject.entity.Users;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UsersMapper {

    @Mapping(target = "password", ignore = true)
    Users toEntity(RegisterFinishDto dto);

    default UserResponseDto toResponseDto(Users user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .userRole(user.getUserRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }

    default UpdateFullNameRequest toFullNameDto(Users users) {
        return UpdateFullNameRequest.builder()
                .name(users.getName())
                .surname(users.getSurname())
                .build();
    }
}
