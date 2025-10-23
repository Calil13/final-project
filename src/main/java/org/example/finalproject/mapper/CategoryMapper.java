package org.example.finalproject.mapper;

import org.example.finalproject.dto.CategoryDto;
import org.example.finalproject.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    default Category toEntity(CategoryDto categoryDto, Category parent) {
        if (categoryDto == null) return null;

        Category category = Category.builder()
                .name(categoryDto.getName())
                .parent(parent)
                .build();

        if (categoryDto.getSubCategory() != null) {
            List<Category> subCategories = categoryDto.getSubCategory()
                    .stream()
                    .map(subDto -> toEntity(subDto, category))
                    .collect(Collectors.toList());
            category.setSubCategories(subCategories);
        }

        return category;

    }

    CategoryDto toDto(Category category);
}
