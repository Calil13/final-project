package org.example.finalproject.mapper;

import org.example.finalproject.dto.ProductReviewsResponseDto;
import org.example.finalproject.entity.ProductReview;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductReviewsMapper {
    ProductReviewsResponseDto toDto(ProductReview review);
}
