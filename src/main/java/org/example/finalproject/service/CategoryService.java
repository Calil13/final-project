package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.CategoryDto;
import org.example.finalproject.entity.Category;
import org.example.finalproject.exception.AlreadyExistsException;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.exception.UnexpectedException;
import org.example.finalproject.mapper.CategoryMapper;
import org.example.finalproject.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getParentCategories() {
        List<Category> category = categoryRepository.findByParentIdIsNull();

        return category.stream()
                .map(c -> CategoryDto.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .build())
                .toList();
    }

    public CategoryDto getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Category with id {} not found", id);
                    return new NotFoundException("Category not found!");
                });

        CategoryDto dto = categoryMapper.toDto(category);

        List<Category> subCategories = categoryRepository.findByParentId(category.getId());

        List<CategoryDto> subCategoriesToDto = subCategories.stream()
                .map(categoryMapper::toDto)
                .toList();

        dto.setSubCategory(subCategoriesToDto);

        return dto;
    }

    public void createCategory(CategoryDto categoryDto) {
        categoryRepository.save(categoryMapper.toEntity(categoryDto, null));
    }

    public void addSubcategory(Long id, CategoryDto subCategory) {
        Category parentCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Parent category not found"));

        Category newSubCategory = categoryMapper.toEntity(subCategory, parentCategory);

        parentCategory.getSubCategories().add(newSubCategory);

        categoryRepository.save(parentCategory);
    }

    @SuppressWarnings("LoggingSimilarMessage")
    public void editCategory(CategoryDto categoryDto, Long id) {

        Category editCategory = categoryRepository.findById(id).orElseThrow(() -> {
            log.error("Category with id {} not found", id);
            return new NotFoundException("Category not found!");
        });

        if (categoryDto.getName() != null) {
            editCategory.setName(categoryDto.getName());
        } else {
            log.error("JSON format is wrong");
            throw new UnexpectedException("JSON only accepts a name parameter");
        }

        categoryRepository.save(editCategory);
    }

    @SuppressWarnings("LoggingSimilarMessage")
    public void deleteCategory(Long id) {
        var category =  categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Category with id {} not found", id);
                    return new NotFoundException("Category not found");
                });

        categoryRepository.delete(category);
    }
}