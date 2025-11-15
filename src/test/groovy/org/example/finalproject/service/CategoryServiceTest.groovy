package org.example.finalproject.service

import org.example.finalproject.dto.CategoryDto
import org.example.finalproject.entity.Category
import org.example.finalproject.mapper.CategoryMapper
import org.example.finalproject.repository.CategoryRepository
import spock.lang.Specification

class CategoryServiceTest extends Specification {

    def categoryRepository = Mock(CategoryRepository)
    def categoryMapper = Mock(CategoryMapper)

    def categoryService = new CategoryService(categoryRepository, categoryMapper)

    def "GetParentCategories"() {
        given: "Parent categories are returned from repository"
        def categories = [Category.builder().id(1L).name("Parent1").build()]
        categoryRepository.findByParentIdIsNull() >> categories

        and: "Mapper converts entity to DTO"
        categoryMapper.toDto(_ as Category) >> { Category c -> CategoryDto.builder().id(c.id).name(c.name).build() }

        when: "Service method is called"
        def result = categoryService.getParentCategories()

        then: "Result contains the parent categories DTO"
        result.size() == 1
        result[0].name == "Parent1"
    }

    def "GetCategoryById"() {
        given: "A category with subcategories"
        def category = Category.builder().id(1L).name("Parent").build()
        def subCategory = Category.builder().id(2L).name("Child").parent(category).build()
        categoryRepository.findById(1L) >> Optional.of(category)
        categoryRepository.findByParentId(1L) >> [subCategory]

        categoryMapper.toDto(category) >> CategoryDto.builder().id(1L).name("Parent").build()
        categoryMapper.toDto(subCategory) >> CategoryDto.builder().id(2L).name("Child").build()

        when:
        def result = categoryService.getCategoryById(1L)

        then:
        result.name == "Parent"
        result.subCategory.size() == 1
        result.subCategory[0].name == "Child"
    }

    def "CreateCategory"() {
        given: "CategoryDto for creation"
        def dto = CategoryDto.builder().name("NewCategory").build()
        categoryMapper.toEntity(dto, null) >> Category.builder().name("NewCategory").build()

        when:
        categoryService.createCategory(dto)

        then:
        1 * categoryRepository.save(_)
    }

    def "AddSubcategory"() {
        given: "Parent category exists"
        def parent = Category.builder().id(1L).name("Parent").subCategories([]).build()
        def dto = CategoryDto.builder().name("Child").build()
        categoryRepository.findById(1L) >> Optional.of(parent)
        categoryMapper.toEntity(dto, parent) >> Category.builder().name("Child").parent(parent).build()

        when:
        categoryService.addSubcategory(1L, dto)

        then:
        1 * categoryRepository.save(parent)
        parent.subCategories.size() == 1
    }

    def "EditCategory"() {
        given: "Existing category"
        def existing = Category.builder().id(1L).name("OldName").build()
        categoryRepository.findById(1L) >> Optional.of(existing)
        def dto = CategoryDto.builder().name("NewName").build()

        when:
        categoryService.editCategory(dto, 1L)

        then:
        existing.name == "NewName"
        1 * categoryRepository.save(existing)
    }

    def "DeleteCategory"() {
        given: "Existing category"
        def existing = Category.builder().id(1L).name("ToDelete").build()
        categoryRepository.findById(1L) >> Optional.of(existing)

        when:
        categoryService.deleteCategory(1L)

        then:
        1 * categoryRepository.delete(existing)
    }
}
