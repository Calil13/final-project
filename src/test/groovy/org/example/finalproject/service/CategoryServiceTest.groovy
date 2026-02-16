package org.example.finalproject.service

import org.example.finalproject.dto.CategoryCreateDto
import org.example.finalproject.dto.CategoryDto
import org.example.finalproject.entity.Category
import org.example.finalproject.entity.Users
import org.example.finalproject.exception.NotFoundException
import org.example.finalproject.mapper.CategoryMapper
import org.example.finalproject.repository.CategoryRepository
import org.example.finalproject.repository.UsersRepository
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

class CategoryServiceTest extends Specification {
    UsersRepository usersRepository = Mock()
    CategoryRepository categoryRepository = Mock()
    CategoryMapper categoryMapper = Mock()

    CategoryService categoryService

    def setup() {
        categoryService = new CategoryService(usersRepository, categoryRepository, categoryMapper)

        def auth = Mock(Authentication)
        auth.getName() >> "admin@test.com"
        def securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> auth
        SecurityContextHolder.setContext(securityContext)
    }

    def "getParentCategories - should return only categories where parentId is null"() {
        given:
        def categories = [
                new Category(id: 1L, name: "Electronics"),
                new Category(id: 2L, name: "Fashion")
        ]
        categoryRepository.findByParentIdIsNull() >> categories

        when:
        def result = categoryService.getParentCategories()

        then:
        result.size() == 2
        result[0].name == "Electronics"
        result[1].name == "Fashion"
    }

    def "getCategoryById - should return category with its subcategories"() {
        given:
        def catId = 1L
        def mainCategory = new Category(id: catId, name: "Main")
        def sub1 = new Category(id: 2L, name: "Sub 1")

        categoryRepository.findById(catId) >> Optional.of(mainCategory)
        categoryRepository.findByParentId(catId) >> [sub1]

        categoryMapper.toDto(mainCategory) >> CategoryDto.builder().name("Main").build()
        categoryMapper.toDto(sub1) >> CategoryDto.builder().name("Sub 1").build()

        when:
        def result = categoryService.getCategoryById(catId)

        then:
        result.name == "Main"
        result.subCategory.size() == 1
        result.subCategory[0].name == "Sub 1"
    }

    def "getCategoryById - should throw NotFoundException when id does not exist"() {
        given:
        categoryRepository.findById(99L) >> Optional.empty()

        when:
        categoryService.getCategoryById(99L)

        then:
        thrown(NotFoundException)
    }

    def "createCategory - should save category when user is authorized"() {
        given:
        def dto = new CategoryCreateDto(name: "New Category")
        def entity = new Category(name: "New Category")

        usersRepository.findByEmail("admin@test.com") >> Optional.of(new Users())
        categoryMapper.toEntity(dto) >> entity

        when:
        categoryService.createCategory(dto)

        then:
        1 * categoryRepository.save(entity)
    }

    def "addSubcategory - should add new subcategory to parent's list"() {
        given:
        def parentId = 1L
        def parentCategory = new Category(id: parentId, name: "Parent", subCategories: [])
        def subDto = new CategoryCreateDto(name: "Sub")
        def subEntity = new Category(name: "Sub")

        usersRepository.findByEmail(_ as String) >> Optional.of(new Users())
        categoryRepository.findById(parentId) >> Optional.of(parentCategory)
        categoryMapper.toEntity(subDto, parentCategory) >> subEntity

        when:
        categoryService.addSubcategory(parentId, subDto)

        then:
        parentCategory.subCategories.contains(subEntity)
        1 * categoryRepository.save(parentCategory)
    }

    def "editCategory - should update name and save"() {
        given:
        def id = 1L
        def existingCategory = new Category(id: id, name: "Old Name")
        def editDto = new CategoryCreateDto(name: "New Name")

        usersRepository.findByEmail(_ as String) >> Optional.of(new Users())
        categoryRepository.findById(id) >> Optional.of(existingCategory)

        when:
        categoryService.editCategory(editDto, id)

        then:
        existingCategory.name == "New Name"
        1 * categoryRepository.save(existingCategory)
    }

    def "deleteCategory - should call delete when category exists"() {
        given:
        def id = 5L
        def category = new Category(id: id, name: "To be deleted")

        usersRepository.findByEmail(_ as String) >> Optional.of(new Users())
        categoryRepository.findById(id) >> Optional.of(category)

        when:
        categoryService.deleteCategory(id)

        then:
        1 * categoryRepository.delete(category)
    }

    def "any method - should throw NotFoundException if current user session is invalid"() {
        given:
        usersRepository.findByEmail(_ as String) >> Optional.empty()

        when:
        categoryService.deleteCategory(1L)

        then:
        thrown(NotFoundException)
    }
}
