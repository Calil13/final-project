package org.example.finalproject.mapper;

import org.example.finalproject.dto.CustomerFinishRegisterDto;
import org.example.finalproject.entity.Users;
import org.example.finalproject.enums.UserRole;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "userRole", expression = "java(UserRole.CUSTOMER)")
    @Mapping(target = "password", ignore = true) // password sonradan set ediləcək
    Users toEntity(CustomerFinishRegisterDto dto);
}
