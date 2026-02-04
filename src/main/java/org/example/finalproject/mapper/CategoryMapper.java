package org.example.finalproject.mapper;

import org.example.finalproject.dto.CategoryCreateDto;
import org.example.finalproject.dto.CategoryDto;
import org.example.finalproject.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryCreateDto createDto);

    @Mapping(target = "name", source = "subCategory.name")
    @Mapping(target = "parent", source = "parent")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subCategories", ignore = true)
    Category toEntity(CategoryCreateDto subCategory, Category parent);

    CategoryDto toDto(Category category);
}
