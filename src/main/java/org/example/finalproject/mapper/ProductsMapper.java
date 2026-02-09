package org.example.finalproject.mapper;

import org.example.finalproject.dto.AddressDto;
import org.example.finalproject.dto.ProductCreateDto;
import org.example.finalproject.dto.ProductUpdateDto;
import org.example.finalproject.dto.ProductResponseDto;
import org.example.finalproject.entity.Address;
import org.example.finalproject.entity.Category;
import org.example.finalproject.entity.Products;
import org.example.finalproject.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductsMapper {

    @Mapping(target = "id", source = "product.id")
    ProductResponseDto toDto(Products product, Address address);
    ProductResponseDto toDto(Products products);

    ProductUpdateDto toDtoUpdate(Products products);
    ProductCreateDto toDtoCreate(Products products);

    default Products toEntity(ProductCreateDto dto, Users owner, Category category) {
        Products product = new Products();

        product.setOwner(owner);
        product.setCategory(category);
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        return product;
    }
}
