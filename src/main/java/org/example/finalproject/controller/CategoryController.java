package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.finalproject.dto.CategoryCreateDto;
import org.example.finalproject.dto.CategoryDto;
import org.example.finalproject.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
@Tag(name = "Category API")
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

    @Operation(
            summary = "Create new category",
            description = "Creates a new parent category."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public void createCategory(@RequestBody CategoryCreateDto createDto) {
        categoryService.createCategory(createDto);
    }

    @Operation(
            summary = "Add subcategory",
            description = "Creates a subcategory under the given parent category ID"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{parentId}/sub")
    public void addSubcategory(@PathVariable Long parentId, @RequestBody CategoryCreateDto subCategory) {
        categoryService.addSubcategory(parentId, subCategory);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{id}")
    public void editCategory(@RequestBody CategoryCreateDto editedDto, @PathVariable Long id) {
        categoryService.editCategory(editedDto, id);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
