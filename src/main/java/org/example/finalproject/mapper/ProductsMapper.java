package org.example.finalproject.mapper;

import org.example.finalproject.dto.ProductRequestDto;
import org.example.finalproject.dto.ProductResponseDto;
import org.example.finalproject.entity.Category;
import org.example.finalproject.entity.Products;
import org.example.finalproject.entity.Vendor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface ProductsMapper {

    default ProductResponseDto toDto(Products product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .storeName(product.getVendor().getStoreName())
                .build();
    }

    ProductRequestDto toDtoRequest(Products products);

    default Products toEntity(ProductRequestDto dto, Vendor vendor, Category category) {
        Products product = new Products();

        product.setVendor(vendor);
        product.setCategory(category);
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        return product;
    }
}
