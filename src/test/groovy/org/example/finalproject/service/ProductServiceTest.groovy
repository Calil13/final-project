package org.example.finalproject.service

import org.example.finalproject.dto.ProductRequestDto
import org.example.finalproject.dto.ProductResponseDto
import org.example.finalproject.entity.Category
import org.example.finalproject.entity.Products
import org.example.finalproject.entity.Vendor
import org.example.finalproject.exception.AccessDeniedException
import org.example.finalproject.exception.NotFoundException
import org.example.finalproject.mapper.ProductsMapper
import org.example.finalproject.repository.CategoryRepository
import org.example.finalproject.repository.ProductRepository
import org.example.finalproject.repository.VendorRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

class ProductServiceTest extends Specification {

    ProductRepository productRepository = Mock()
    ProductsMapper productsMapper = Mock()
    VendorRepository vendorRepository = Mock()
    CategoryRepository categoryRepository = Mock()

    ProductService service = new ProductService(
            productRepository,
            productsMapper,
            vendorRepository,
            categoryRepository
    )

    def "getProducts returns paginated products"() {
        given:
        def pageable = PageRequest.of(0, 10)
        def product = new Products()
        Page<Products> productPage = new PageImpl([product])

        productRepository.findAll(pageable) >> productPage
        productsMapper.toDto(product) >> new ProductResponseDto()

        when:
        def result = service.getProducts(pageable)

        then:
        result.totalElements == 1
    }

    def "getProduct returns product when found"() {
        given:
        def product = new Products()
        productRepository.findById(1L) >> Optional.of(product)
        productsMapper.toDto(product) >> new ProductResponseDto()

        when:
        def result = service.getProduct(1L)

        then:
        result != null
    }

    def "getProduct throws NotFoundException when not found"() {
        given:
        productRepository.findById(1L) >> Optional.empty()

        when:
        service.getProduct(1L)

        then:
        thrown(NotFoundException)
    }

    def "getProductsByCategory returns correct results"() {
        given:
        def pageable = PageRequest.of(0, 10)
        def product = new Products()
        Page<Products> productPage = new PageImpl([product])

        productRepository.findByCategoryId(5L, pageable) >> productPage
        productsMapper.toDto(product) >> new ProductResponseDto()

        when:
        def result = service.getProductsByCategory(5L, pageable)

        then:
        result.content.size() == 1
    }

    def "getVendorProducts returns vendor product list"() {
        given:
        def pageable = PageRequest.of(0, 10)
        def product = new Products()
        Page<Products> productPage = new PageImpl([product])

        productRepository.findByVendorId(3L, pageable) >> productPage
        productsMapper.toDto(product) >> new ProductResponseDto()

        when:
        def result = service.getVendorProducts(3L, pageable)

        then:
        result.totalElements == 1
    }

    def "addProduct successfully saves and returns dto"() {
        given:
        def dto = new ProductRequestDto(vendorId: 2L, categoryId: 3L)

        def vendor = new Vendor(id: 2L)
        def category = new Category(id: 3L)
        def product = new Products()

        vendorRepository.findById(2L) >> Optional.of(vendor)
        categoryRepository.findById(3L) >> Optional.of(category)
        productsMapper.toEntity(dto, vendor, category) >> product
        productsMapper.toDtoRequest(product) >> dto

        when:
        def result = service.addProduct(dto)

        then:
        result != null
        1 * productRepository.save(product)
    }

    def "addProduct throws NotFoundException when vendor not found"() {
        given:
        def dto = new ProductRequestDto(vendorId: 99L)

        vendorRepository.findById(99L) >> Optional.empty()

        when:
        service.addProduct(dto)

        then:
        thrown(NotFoundException)
    }

    def "addProduct throws NotFoundException when category not found"() {
        given:
        def dto = new ProductRequestDto(vendorId: 1L, categoryId: 2L)

        vendorRepository.findById(1L) >> Optional.of(new Vendor())
        categoryRepository.findById(2L) >> Optional.empty()

        when:
        service.addProduct(dto)

        then:
        thrown(NotFoundException)
    }

    def "editProduct updates product when vendor matches"() {
        given:
        def dto = new ProductRequestDto(
                productId: 1L,
                vendorId: 10L,
                categoryId: 20L,
                name: "Updated"
        )

        def vendor = new Vendor(id: 10L)
        def category = new Category(id: 20L)
        def product = new Products(vendor: vendor)

        productRepository.findById(1L) >> Optional.of(product)
        categoryRepository.findById(20L) >> Optional.of(category)
        productsMapper.toDtoRequest(product) >> dto

        when:
        def result = service.editProduct(dto)

        then:
        result != null
        product.name == "Updated"
        product.category == category
        1 * productRepository.save(product)
    }

    def "editProduct throws AccessDenied when vendor mismatch"() {
        given:
        def dto = new ProductRequestDto(productId: 1L, vendorId: 99L)

        def vendor = new Vendor(id: 10L)
        def product = new Products(vendor: vendor)

        productRepository.findById(1L) >> Optional.of(product)

        when:
        service.editProduct(dto)

        then:
        thrown(AccessDeniedException)
    }

    def "editProduct throws NotFoundException when product not found"() {
        given:
        productRepository.findById(123L) >> Optional.empty()
        def dto = new ProductRequestDto(productId: 123L)

        when:
        service.editProduct(dto)

        then:
        thrown(NotFoundException)
    }
}
