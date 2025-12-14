package org.example.finalproject.service

import org.example.finalproject.dto.ProductRequestDto
import org.example.finalproject.dto.ProductResponseDto
import org.example.finalproject.entity.*
import org.example.finalproject.exception.AccessDeniedException
import org.example.finalproject.exception.NotFoundException
import org.example.finalproject.mapper.ProductsMapper
import org.example.finalproject.repository.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification
import org.springframework.data.domain.Pageable

class ProductsServiceTest extends Specification {

    ProductsRepository productRepository = Mock()
    ProductsMapper productsMapper = Mock()
    VendorRepository vendorRepository = Mock()
    CategoryRepository categoryRepository = Mock()
    UsersRepository usersRepository = Mock()

    ProductsService productsService = new ProductsService(
            productRepository,
            productsMapper,
            vendorRepository,
            categoryRepository,
            usersRepository
    )

    def setup() {
        SecurityContext securityContext = Mock()
        Authentication authentication = Mock()

        authentication.getName() >> "vendor@test.com"
        securityContext.getAuthentication() >> authentication
        SecurityContextHolder.setContext(securityContext)
    }

    def cleanup() {
        SecurityContextHolder.clearContext()
    }

    def "should return paged products"() {
        given:
        Pageable pageable = PageRequest.of(0, 10)
        Products product = new Products(id: 1L)
        Page<Products> page = new PageImpl([product])
        ProductResponseDto dto = new ProductResponseDto()

        productRepository.findAll(pageable) >> page
        productsMapper.toDto(product) >> dto

        when:
        Page<ProductResponseDto> result = productsService.getProducts(pageable)

        then:
        result.content.size() == 1
        result.content[0] == dto
    }

    def "should return product by id"() {
        given:
        Products product = new Products(id: 1L)
        ProductResponseDto dto = new ProductResponseDto()

        productRepository.findById(1L) >> Optional.of(product)
        productsMapper.toDto(product) >> dto

        when:
        def result = productsService.getProduct(1L)

        then:
        result == dto
    }

    def "should throw NotFoundException when product not found"() {
        given:
        productRepository.findById(99L) >> Optional.empty()

        when:
        productsService.getProduct(99L)

        then:
        thrown(NotFoundException)
    }


    def "should add product successfully"() {
        given:
        ProductRequestDto request = new ProductRequestDto(
                vendorId: 1L,
                categoryId: 2L
        )

        Vendor vendor = new Vendor(id: 1L)
        Category category = new Category(id: 2L)
        Products product = new Products()

        vendorRepository.findById(1L) >> Optional.of(vendor)
        categoryRepository.findById(2L) >> Optional.of(category)
        productsMapper.toEntity(request, vendor, category) >> product
        productsMapper.toDtoRequest(product) >> request

        when:
        def result = productsService.addProduct(request)

        then:
        1 * productRepository.save(product)
        result == request
    }

    def "should edit product successfully"() {
        given:
        ProductRequestDto request = new ProductRequestDto(
                productId: 1L,
                vendorId: 1L,
                categoryId: 2L,
                name: "New name"
        )

        Vendor vendor = new Vendor(id: 1L)
        Category category = new Category(id: 2L)
        Products product = new Products(id: 1L, vendor: vendor)

        productRepository.findById(1L) >> Optional.of(product)
        categoryRepository.findById(2L) >> Optional.of(category)
        productsMapper.toDtoRequest(product) >> request

        when:
        def result = productsService.editProduct(request)

        then:
        product.name == "New name"
        1 * productRepository.save(product)
        result == request
    }

    def "should throw AccessDeniedException when editing чужой product"() {
        given:
        ProductRequestDto request = new ProductRequestDto(
                productId: 1L,
                vendorId: 2L
        )

        Vendor vendor = new Vendor(id: 1L)
        Products product = new Products(id: 1L, vendor: vendor)

        productRepository.findById(1L) >> Optional.of(product)

        when:
        productsService.editProduct(request)

        then:
        thrown(AccessDeniedException)
    }

    def "should delete product when vendor owns it"() {
        given:
        Users user = new Users(email: "vendor@test.com")
        Vendor vendor = new Vendor(id: 1L, user: user)
        Products product = new Products(id: 1L, vendor: vendor)

        usersRepository.findByEmail("vendor@test.com") >> Optional.of(user)
        vendorRepository.findByUser(user) >> Optional.of(vendor)
        productRepository.findById(1L) >> Optional.of(product)

        when:
        String result = productsService.deleteProduct(1L)

        then:
        1 * productRepository.delete(product)
        result == "Product deleted successfully!"
    }

    def "should throw AccessDeniedException when vendor does not own product"() {
        given:
        Users user = new Users(email: "vendor@test.com")
        Vendor vendor = new Vendor(id: 1L, user: user)
        Vendor otherVendor = new Vendor(id: 2L)
        Products product = new Products(id: 1L, vendor: otherVendor)

        usersRepository.findByEmail("vendor@test.com") >> Optional.of(user)
        vendorRepository.findByUser(user) >> Optional.of(vendor)
        productRepository.findById(1L) >> Optional.of(product)

        when:
        productsService.deleteProduct(1L)

        then:
        thrown(AccessDeniedException)
        0 * productRepository.delete(_)
    }
}

