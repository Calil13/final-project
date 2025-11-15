package org.example.finalproject.mapper;

import org.example.finalproject.dto.ProductResponseDto;
import org.example.finalproject.entity.Products;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductsMapper {

    default ProductResponseDto toDto(Products product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryName(product.getCategory().getName())
                .vendorName(product.getVendor().getUser().getName())
                .createdAt(product.getCreatedAt())
                .build();
    }
}
