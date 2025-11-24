package org.example.finalproject.controller;

import lombok.AllArgsConstructor;
import org.example.finalproject.dto.CategoryDto;
import org.example.finalproject.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/final-project/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getParentCategories() {
        return categoryService.getParentCategories();
    }

    @GetMapping("/{categoryId}")
    public CategoryDto getCategoryById(@PathVariable Long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createCategory(@RequestBody CategoryDto categoryDto) {
        categoryService.createCategory(categoryDto);
    }

    @PostMapping("/{parentId}/sub")
    @ResponseStatus(HttpStatus.CREATED)
    public void addSubcategory(@PathVariable Long parentId, @RequestBody CategoryDto subCategory) {
        categoryService.addSubcategory(parentId, subCategory);
    }

    @PatchMapping("/{id}")
    public void editCategory(@RequestBody CategoryDto categoryDto, @PathVariable Long id) {
        categoryService.editCategory(categoryDto, id);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
