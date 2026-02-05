package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.CategoryCreateDto;
import org.example.finalproject.dto.CategoryDto;
import org.example.finalproject.entity.Category;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.exception.UnexpectedException;
import org.example.finalproject.mapper.CategoryMapper;
import org.example.finalproject.repository.CategoryRepository;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryService {

    private final UsersRepository usersRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> getParentCategories() {
        List<Category> category = categoryRepository.findByParentIdIsNull();

        return category.stream()
                .map(c -> CategoryDto.builder()
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

    public void createCategory(CategoryCreateDto createDto) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        log.info("A new category has been created.");

        categoryRepository.save(categoryMapper.toEntity(createDto));
    }

    public void addSubcategory(Long id, CategoryCreateDto subCategory) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        Category parentCategory = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Parent category not found!");
                    return new  NotFoundException("Parent category not found!");
                });

        Category newSubCategory = categoryMapper.toEntity(subCategory, parentCategory);

        parentCategory.getSubCategories().add(newSubCategory);

        categoryRepository.save(parentCategory);
    }

    @SuppressWarnings("LoggingSimilarMessage")
    public void editCategory(CategoryCreateDto editedDto, Long id) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        var category = categoryRepository.findById(id).orElseThrow(() -> {
            log.error("Category with Id {} not found", id);
            return new NotFoundException("Category not found!");
        });

        category.setName(editedDto.getName());

        log.info("Category edited. \nId: {}\nOld Name: {}\nNew Name: {}", id, category.getName(), editedDto.getName());

        categoryRepository.save(category);
    }

    @SuppressWarnings("LoggingSimilarMessage")
    public void deleteCategory(Long id) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        var category =  categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Category with id {} not found :", id);
                    return new NotFoundException("Category not found!");
                });

        log.info("Category deleted.\nId: {}\nName: {}", id, category.getName());
        categoryRepository.delete(category);
    }
}