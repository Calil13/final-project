package org.example.finalproject.mapper;

import org.example.finalproject.dto.RegisterFinishDto;
import org.example.finalproject.entity.Users;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UsersMapper {

    @Mapping(target = "password", ignore = true) // password sonradan set ediləcək
    Users toEntity(RegisterFinishDto dto);
}
