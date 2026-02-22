package org.example.finalproject.service

import org.example.finalproject.dto.ProductReviewsResponseDto
import org.example.finalproject.entity.ProductReview
import org.example.finalproject.entity.Products
import org.example.finalproject.entity.Users
import org.example.finalproject.exception.IllegalStateException
import org.example.finalproject.exception.NotFoundException
import org.example.finalproject.mapper.ProductReviewsMapper
import org.example.finalproject.repository.ProductReviewRepository
import org.example.finalproject.repository.ProductsRepository
import org.example.finalproject.repository.UsersRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

class ProductReviewsServiceTest extends Specification {

    def usersRepository = Mock(UsersRepository)
    def productsRepository = Mock(ProductsRepository)
    def productReviewRepository = Mock(ProductReviewRepository)
    def productReviewsMapper = Mock(ProductReviewsMapper)

    def service = new ProductReviewsService(
            usersRepository,
            productsRepository,
            productReviewRepository,
            productReviewsMapper
    )

    def setup() {
        def auth = new UsernamePasswordAuthenticationToken("test@mail.com", null)
        SecurityContextHolder.context.authentication = auth
    }

    def cleanup() {
        SecurityContextHolder.clearContext()
    }

    def "getReviews should return mapped page"() {
        given:
        def product = new Products(id: 1L)
        def user = new Users(id: 2L, name: "John")
        def review = new ProductReview(id: 3L, comment: "Good", product: product, customer: user)

        def pageable = PageRequest.of(0, 10)
        def page = new PageImpl<ProductReview>([review])

        productsRepository.findById(1L) >> Optional.of(product)
        productReviewRepository.findReviewsByProduct(product, pageable) >> page
        productReviewsMapper.toDto(review, "John") >> new ProductReviewsResponseDto()

        when:
        def result = service.getReviews(pageable, 1L)

        then:
        result.content.size() == 1
        1 * productReviewsMapper.toDto(review, "John")
    }

    def "getReviews should throw when product not found"() {
        given:
        productsRepository.findById(1L) >> Optional.empty()

        when:
        service.getReviews(PageRequest.of(0, 10), 1L)

        then:
        thrown(NotFoundException)
    }

    def "addReviews should save review when product active"() {
        given:
        def user = new Users(id: 1L, email: "test@mail.com")
        def product = new Products(id: 2L, isAvailable: true)

        usersRepository.findByEmailAndDeletedFalse("test@mail.com") >> Optional.of(user)
        productsRepository.findById(2L) >> Optional.of(product)

        when:
        service.addReviews(2L, "Nice product")

        then:
        1 * productReviewRepository.save(_ as ProductReview)
    }

    def "addReviews should throw when product inactive"() {
        given:
        def user = new Users(id: 1L)
        def product = new Products(id: 2L, isAvailable: false)

        usersRepository.findByEmailAndDeletedFalse("test@mail.com") >> Optional.of(user)
        productsRepository.findById(2L) >> Optional.of(product)

        when:
        service.addReviews(2L, "Bad")

        then:
        thrown(IllegalStateException)
    }

    def "editReviews should update comment when owner"() {
        given:
        def user = new Users(id: 1L, email: "test@mail.com")
        def review = new ProductReview(id: 5L, comment: "Old")
        review.customer = user

        usersRepository.findByEmailAndDeletedFalse("test@mail.com") >> Optional.of(user)
        productReviewRepository.findById(5L) >> Optional.of(review)

        when:
        service.editReviews(5L, "Updated")

        then:
        review.comment == "Updated"
        1 * productReviewRepository.save(review)
    }

    def "editReviews should throw when not owner"() {
        given:
        def user = new Users(id: 1L, email: "test@mail.com")
        def otherUser = new Users(id: 2L)
        def review = new ProductReview(id: 5L)
        review.customer = otherUser

        usersRepository.findByEmailAndDeletedFalse("test@mail.com") >> Optional.of(user)
        productReviewRepository.findById(5L) >> Optional.of(review)

        when:
        service.editReviews(5L, "Hack")

        then:
        thrown(IllegalStateException)
    }

    def "deleteReview should delete when owner"() {
        given:
        def user = new Users(id: 1L, email: "test@mail.com")
        def review = new ProductReview(id: 10L)
        review.customer = user

        usersRepository.findByEmailAndDeletedFalse("test@mail.com") >> Optional.of(user)
        productReviewRepository.findById(10L) >> Optional.of(review)

        when:
        def result = service.deleteReview(10L)

        then:
        result == "Review deleted."
        1 * productReviewRepository.delete(review)
    }

    def "deleteReview should throw when not owner"() {
        given:
        def user = new Users(id: 1L, email: "test@mail.com")
        def otherUser = new Users(id: 2L)
        def review = new ProductReview(id: 10L)
        review.customer = otherUser

        usersRepository.findByEmailAndDeletedFalse("test@mail.com") >> Optional.of(user)
        productReviewRepository.findById(10L) >> Optional.of(review)

        when:
        service.deleteReview(10L)

        then:
        thrown(IllegalStateException)
    }
}

