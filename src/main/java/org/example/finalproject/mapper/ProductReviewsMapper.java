package org.example.finalproject.mapper;

import org.example.finalproject.dto.ProductReviewsResponseDto;
import org.example.finalproject.dto.UserResponseDto;
import org.example.finalproject.entity.ProductReview;
import org.example.finalproject.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductReviewsMapper {

    @Mapping(target = "userName", source = "name")
    ProductReviewsResponseDto toDto(ProductReview review, String name);
}
