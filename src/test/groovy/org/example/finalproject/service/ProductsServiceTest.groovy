package org.example.finalproject.service

import org.example.finalproject.dto.ProductCreateDto
import org.example.finalproject.dto.ProductResponseDto
import org.example.finalproject.dto.ProductUpdateDto
import org.example.finalproject.entity.Address
import org.example.finalproject.entity.Category
import org.example.finalproject.entity.Products
import org.example.finalproject.entity.Users
import org.example.finalproject.enums.UserRole
import org.example.finalproject.exception.InvalidCategoryOperationException
import org.example.finalproject.exception.NotFoundException
import org.example.finalproject.exception.ProductInUseException
import org.example.finalproject.mapper.ProductsMapper
import org.example.finalproject.repository.AddressRepository
import org.example.finalproject.repository.CategoryRepository
import org.example.finalproject.repository.ProductsRepository
import org.example.finalproject.repository.UsersRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

class ProductsServiceTest extends Specification {

    def productRepository = Mock(ProductsRepository)
    def productsMapper = Mock(ProductsMapper)
    def categoryRepository = Mock(CategoryRepository)
    def usersRepository = Mock(UsersRepository)
    def addressRepository = Mock(AddressRepository)

    def service = new ProductsService(
            productRepository,
            productsMapper,
            categoryRepository,
            usersRepository,
            addressRepository
    )

    def setup() {
        def auth = new UsernamePasswordAuthenticationToken("owner@mail.com", null)
        SecurityContextHolder.context.authentication = auth
    }

    def cleanup() {
        SecurityContextHolder.clearContext()
    }

    def "getProduct should return dto when product exists"() {
        given:
        def owner = new Users(id: 1L)
        def product = new Products(id: 10L, owner: owner)
        def address = Mock(Address)

        productRepository.findById(10L) >> Optional.of(product)
        addressRepository.findByUser(owner) >> Optional.of(address)
        productsMapper.toDto(product, address) >> new ProductResponseDto()

        when:
        def result = service.getProduct(10L)

        then:
        result != null
    }

    def "getProduct should throw when not found"() {
        given:
        productRepository.findById(99L) >> Optional.empty()

        when:
        service.getProduct(99L)

        then:
        thrown(NotFoundException)
    }

    def "addProduct should save when category is leaf"() {
        given:
        def owner = new Users(id: 1L, email: "owner@mail.com")
        def category = new Category(id: 5L, parent: new Category())
        def dto = new ProductCreateDto(categoryId: 5L)

        usersRepository.findByEmail("owner@mail.com") >> Optional.of(owner)
        categoryRepository.findById(5L) >> Optional.of(category)

        def product = new Products()
        productsMapper.toEntity(dto, owner, category) >> product
        productsMapper.toDtoCreate(product) >> dto

        when:
        def result = service.addProduct(dto)

        then:
        1 * productRepository.save(product)
        result == dto
    }

    def "addProduct should throw when category is parent"() {
        given:
        def owner = new Users(id: 1L)
        def category = new Category(id: 5L, parent: null)
        def dto = new ProductCreateDto(categoryId: 5L)

        usersRepository.findByEmail("owner@mail.com") >> Optional.of(owner)
        categoryRepository.findById(5L) >> Optional.of(category)

        when:
        service.addProduct(dto)

        then:
        thrown(InvalidCategoryOperationException)
    }

    def "editProduct should update when owner"() {
        given:
        def owner = new Users(id: 1L, email: "owner@mail.com")
        def product = new Products(id: 10L, owner: owner, name: "Old")

        def category = new Category(id: 3L)

        usersRepository.findByEmail("owner@mail.com") >> Optional.of(owner)
        productRepository.findById(10L) >> Optional.of(product)
        categoryRepository.findById(3L) >> Optional.of(category)

        def updateDto = new ProductUpdateDto(
                productId: 10L,
                name: "NewName",
                categoryId: 3L
        )

        productsMapper.toDtoUpdate(product) >> updateDto

        when:
        def result = service.editProduct(updateDto)

        then:
        product.name == "NewName"
        product.category == category
        1 * productRepository.save(product)
        result == updateDto
    }

    def "editProduct should throw when not owner"() {
        given:
        def owner = new Users(id: 1L, email: "owner@mail.com")
        def other = new Users(id: 2L, email: "other@mail.com")
        def product = new Products(id: 10L, owner: other)

        usersRepository.findByEmail("owner@mail.com") >> Optional.of(owner)
        productRepository.findById(10L) >> Optional.of(product)

        def dto = new ProductUpdateDto(productId: 10L)

        when:
        service.editProduct(dto)

        then:
        thrown(ProductInUseException)
    }

    def "deleteProduct should delete when owner"() {
        given:
        def owner = new Users(id: 1L, email: "owner@mail.com", userRole: UserRole.OWNER)
        def product = new Products(id: 10L, owner: owner, isAvailable: true)

        usersRepository.findByEmail("owner@mail.com") >> Optional.of(owner)
        productRepository.findById(10L) >> Optional.of(product)

        when:
        def result = service.deleteProduct(10L)

        then:
        1 * productRepository.delete(product)
        result == "Product deleted successfully."
    }

    def "deleteProduct should throw when not owner and not admin"() {
        given:
        def user = new Users(id: 1L, email: "owner@mail.com", userRole: UserRole.OWNER)
        def other = new Users(id: 2L)
        def product = new Products(id: 10L, owner: other, isAvailable: true)

        usersRepository.findByEmail("owner@mail.com") >> Optional.of(user)
        productRepository.findById(10L) >> Optional.of(product)

        when:
        service.deleteProduct(10L)

        then:
        thrown(ProductInUseException)
    }

    def "deleteProduct should throw when product unavailable"() {
        given:
        def owner = new Users(id: 1L, email: "owner@mail.com", userRole: UserRole.ADMIN)
        def product = new Products(id: 10L, owner: owner, isAvailable: false)

        usersRepository.findByEmail("owner@mail.com") >> Optional.of(owner)
        productRepository.findById(10L) >> Optional.of(product)

        when:
        service.deleteProduct(10L)

        then:
        thrown(ProductInUseException)
    }
}
