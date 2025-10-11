package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.entity.Category;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> {
            log.error("Category with id {} not found", id);
            return new NotFoundException("Category not found!");
        });
    }

    public void createCategory(Category category) {
        categoryRepository.save(category);
    }
}
