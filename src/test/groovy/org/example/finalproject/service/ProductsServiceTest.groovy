package org.example.finalproject.service

import org.example.finalproject.dto.ProductCreateDto
import org.example.finalproject.dto.ProductUpdateDto
import org.example.finalproject.dto.ProductResponseDto
import org.example.finalproject.entity.*
import org.example.finalproject.enums.UserRole
import org.example.finalproject.exception.*
import org.example.finalproject.mapper.ProductsMapper
import org.example.finalproject.repository.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
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

    def setupSecurity(String email) {
        Authentication auth = Mock()
        auth.getName() >> email

        SecurityContext context = Mock()
        context.getAuthentication() >> auth

        SecurityContextHolder.setContext(context)
    }

    def "should return products page"() {

        given:

        def pageable = PageRequest.of(0,10)

        def product = new Products()
        def dto = new ProductResponseDto()

        productRepository.findByIsAvailableTrue(pageable) >> new PageImpl([product])
        productsMapper.toDto(product) >> dto

        when:

        def result = service.getProducts(pageable)

        then:

        result.content.size() == 1
    }

    def "should return product by id"() {

        given:

        def user = new Users(id:1)
        def product = new Products(id:10, owner:user)
        def address = new Address()
        def dto = new ProductResponseDto()

        productRepository.findById(10) >> Optional.of(product)
        addressRepository.findByUser(user) >> Optional.of(address)
        productsMapper.toDto(product,address) >> dto

        when:

        def result = service.getProduct(10)

        then:

        result == dto
    }

    def "should throw exception if product not found"() {

        given:

        productRepository.findById(1) >> Optional.empty()

        when:

        service.getProduct(1)

        then:

        thrown(NotFoundException)
    }

    def "should return products by category"() {

        given:

        def pageable = PageRequest.of(0,10)

        def parent = new Category()
        def category = new Category(id:5,parent:parent)

        def product = new Products()
        def dto = new ProductResponseDto()

        categoryRepository.findById(5) >> Optional.of(category)
        productRepository.findByCategoryId(5,pageable) >> new PageImpl([product])
        productsMapper.toDto(product) >> dto

        when:

        def result = service.getProductsByCategory(5,pageable)

        then:

        result.content.size() == 1
    }

    def "should throw error if parent category sent to subcategory endpoint"() {

        given:

        def category = new Category(id:2,parent:null)

        categoryRepository.findById(2) >> Optional.of(category)

        when:

        service.getProductsByCategory(2,PageRequest.of(0,10))

        then:

        thrown(IllegalArgumentException)
    }

    def "should return products by parent category"() {

        given:

        def pageable = PageRequest.of(0,10)

        def category = new Category(id:3,parent:null)
        def product = new Products()
        def dto = new ProductResponseDto()

        categoryRepository.findById(3) >> Optional.of(category)
        productRepository.findByCategoryParentId(3,pageable) >> new PageImpl([product])
        productsMapper.toDto(product) >> dto

        when:

        def result = service.getProductsByParentCategory(3,pageable)

        then:

        result.content.size() == 1
    }

    def "should throw error if subcategory sent to parent endpoint"() {

        given:

        def parent = new Category()
        def category = new Category(parent:parent)

        categoryRepository.findById(4) >> Optional.of(category)

        when:

        service.getProductsByParentCategory(4,PageRequest.of(0,10))

        then:

        thrown(IllegalArgumentException)
    }

    def "should return owner products"() {

        given:

        def pageable = PageRequest.of(0,10)

        def user = new Users(id:1,userRole:UserRole.OWNER)

        def product = new Products()
        def dto = new ProductResponseDto()

        usersRepository.findById(1) >> Optional.of(user)
        productRepository.findByOwnerId(1,pageable) >> new PageImpl([product])
        productsMapper.toDto(product) >> dto

        when:

        def result = service.getOwnerProducts(1,pageable)

        then:

        result.content.size() == 1
    }

    def "should throw error if user is not owner"() {

        given:

        def user = new Users(userRole:UserRole.CUSTOMER)

        usersRepository.findById(1) >> Optional.of(user)

        when:

        service.getOwnerProducts(1,PageRequest.of(0,10))

        then:

        thrown(AccessDeniedException)
    }

    def "should add product"() {

        given:

        setupSecurity("owner@mail.com")

        def user = new Users(id:1)
        def parent = new Category()
        def category = new Category(id:5,parent:parent)

        def dto = new ProductCreateDto(categoryId:5)

        def product = new Products()

        usersRepository.findByEmailAndDeletedFalse("owner@mail.com") >> Optional.of(user)
        categoryRepository.findById(5) >> Optional.of(category)
        productsMapper.toEntity(dto,user,category) >> product
        productsMapper.toDtoCreate(product) >> dto

        when:

        def result = service.addProduct(dto)

        then:

        result == dto
    }

    def "should not add product to parent category"() {

        given:

        setupSecurity("owner@mail.com")

        def user = new Users()
        def category = new Category(parent:null)

        def dto = new ProductCreateDto(categoryId:2)

        usersRepository.findByEmailAndDeletedFalse("owner@mail.com") >> Optional.of(user)
        categoryRepository.findById(2) >> Optional.of(category)

        when:

        service.addProduct(dto)

        then:

        thrown(InvalidCategoryOperationException)
    }

    def "should edit product"() {

        given:

        setupSecurity("owner@mail.com")

        def owner = new Users(email:"owner@mail.com")
        def product = new Products(id:1,owner:owner)

        def category = new Category()

        def dto = new ProductUpdateDto(
                productId:1,
                categoryId:2,
                name:"Phone",
                description:"Good",
                price:100
        )

        usersRepository.findByEmailAndDeletedFalse("owner@mail.com") >> Optional.of(owner)
        productRepository.findById(1) >> Optional.of(product)
        categoryRepository.findById(2) >> Optional.of(category)

        productsMapper.toDtoUpdate(product) >> dto

        when:

        def result = service.editProduct(dto)

        then:

        result == dto
    }

    def "should not edit product of another user"() {

        given:

        setupSecurity("user@mail.com")

        def owner = new Users(email:"owner@mail.com")
        def product = new Products(owner:owner)

        def dto = new ProductUpdateDto(productId:1)

        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(new Users())

        productRepository.findById(1) >> Optional.of(product)

        when:

        service.editProduct(dto)

        then:

        thrown(AccessDeniedException)
    }

    def "should delete product by owner"() {

        given:

        setupSecurity("owner@mail.com")

        def owner = new Users(id:1,email:"owner@mail.com",userRole:UserRole.OWNER)
        def product = new Products(id:1,owner:owner,isAvailable:true,name:"Phone")

        usersRepository.findByEmailAndDeletedFalse("owner@mail.com") >> Optional.of(owner)
        productRepository.findById(1) >> Optional.of(product)

        when:

        def result = service.deleteProduct(1)

        then:

        result == "Product deleted successfully."
    }

    def "should not delete product if not owner or admin"() {

        given:

        setupSecurity("user@mail.com")

        def owner = new Users(id:2,email:"owner@mail.com")
        def user = new Users(id:1,email:"user@mail.com",userRole:UserRole.CUSTOMER)

        def product = new Products(owner:owner,isAvailable:true)

        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        productRepository.findById(1) >> Optional.of(product)

        when:

        service.deleteProduct(1)

        then:

        thrown(AccessDeniedException)
    }

    def "should not delete unavailable product"() {

        given:

        setupSecurity("owner@mail.com")

        def owner = new Users(id:1,email:"owner@mail.com",userRole:UserRole.OWNER)
        def product = new Products(owner:owner,isAvailable:false)

        usersRepository.findByEmailAndDeletedFalse("owner@mail.com") >> Optional.of(owner)
        productRepository.findById(1) >> Optional.of(product)

        when:

        service.deleteProduct(1)

        then:

        thrown(ProductInUseException)
    }

}
