package org.example.finalproject.mapper;

import org.example.finalproject.dto.ProductRequestDto;
import org.example.finalproject.dto.ProductResponseDto;
import org.example.finalproject.entity.Category;
import org.example.finalproject.entity.Products;
import org.example.finalproject.entity.Users;
import org.example.finalproject.repository.UsersRepository;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductsMapper {

    ProductResponseDto toDto(Products product);

    ProductRequestDto toDtoRequest(Products products);

    default Products toEntity(ProductRequestDto dto, Users owner, Category category) {
        Products product = new Products();

        product.setOwner(owner);
        product.setCategory(category);
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());

        return product;
    }
}
