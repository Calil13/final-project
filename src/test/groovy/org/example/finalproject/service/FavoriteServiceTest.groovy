package org.example.finalproject.service

import org.example.finalproject.dto.FavoriteProductsDto
import org.example.finalproject.entity.Favorites
import org.example.finalproject.entity.Products
import org.example.finalproject.entity.Users
import org.example.finalproject.exception.AlreadyExistsException
import org.example.finalproject.exception.IllegalStateException
import org.example.finalproject.exception.NotFoundException
import org.example.finalproject.mapper.FavoritesMapper
import org.example.finalproject.repository.FavoriteRepository
import org.example.finalproject.repository.ProductsRepository
import org.example.finalproject.repository.UsersRepository
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

class FavoriteServiceTest extends Specification {
    UsersRepository usersRepository = Mock()
    FavoriteRepository favoriteRepository = Mock()
    FavoritesMapper favoritesMapper = Mock()
    ProductsRepository productsRepository = Mock()

    FavoriteService favoriteService = new FavoriteService(
            usersRepository, favoriteRepository, favoritesMapper, productsRepository
    )

    def "getFavorites should return a paged list of favorites for the current user"() {
        given: "an authenticated user and their favorites"
        def email = "test@user.com"
        def user = new Users(email: email)
        def pageable = PageRequest.of(0, 10)
        def favoritesList = [new Favorites(), new Favorites()]
        def favoritesPage = new PageImpl<>(favoritesList)

        setupSecurityContext(email)

        when: "getFavorites is called"
        def result = favoriteService.getFavorites(pageable)

        then: "verify the flow"
        1 * usersRepository.findByEmail(email) >> Optional.of(user)
        1 * favoriteRepository.findByCustomer(user, pageable) >> favoritesPage
        2 * favoritesMapper.toDto(_ as Favorites) >> new FavoriteProductsDto()

        and: "result contains mapped items"
        result.content.size() == 2
    }

    def "addFavorite should throw exception if product is already in favorites"() {
        given: "a user and a product already marked as favorite"
        def email = "user@test.com"
        def productId = 1L
        def user = new Users(email: email)
        def product = new Products(id: productId)

        setupSecurityContext(email)

        when: "addFavorite is called"
        favoriteService.addFavorite(productId)

        then: "check for existence and throw exception"
        1 * usersRepository.findByEmail(email) >> Optional.of(user)
        1 * productsRepository.findById(productId) >> Optional.of(product)
        1 * favoriteRepository.existsByCustomerAndProduct(user, product) >> true

        thrown(AlreadyExistsException)
    }

    def "addFavorite should throw exception if product is inactive"() {
        given: "an inactive product"
        def email = "user@test.com"
        def productId = 1L
        def user = new Users(email: email)
        def product = new Products(id: productId, isAvailable: false)

        setupSecurityContext(email)

        when: "trying to add an inactive product"
        favoriteService.addFavorite(productId)

        then: "IllegalStateException is thrown"
        1 * usersRepository.findByEmail(email) >> Optional.of(user)
        1 * productsRepository.findById(productId) >> Optional.of(product)
        1 * favoriteRepository.existsByCustomerAndProduct(user, product) >> false

        thrown(IllegalStateException)
    }

    def "addFavorite should save successfully for valid inputs"() {
        given: "a valid user and available product"
        def email = "user@test.com"
        def productId = 1L
        def user = new Users(email: email)
        def product = new Products(id: productId, isAvailable: true)

        setupSecurityContext(email)

        when: "adding a new favorite"
        favoriteService.addFavorite(productId)

        then: "verify the entity is saved"
        1 * usersRepository.findByEmail(email) >> Optional.of(user)
        1 * productsRepository.findById(productId) >> Optional.of(product)
        1 * favoriteRepository.existsByCustomerAndProduct(user, product) >> false
        1 * favoriteRepository.save({ Favorites fav ->
            fav.customer == user && fav.product == product
        })
    }

    def "deleteFavorite should remove the item if it exists"() {
        given: "an existing favorite item"
        def email = "user@test.com"
        def favoriteId = 50L
        def favorite = new Favorites(id: favoriteId)

        setupSecurityContext(email)

        when: "deleting a favorite"
        favoriteService.deleteFavorite(favoriteId)

        then: "check user and delete from repo"
        1 * usersRepository.findByEmail(email) >> Optional.of(new Users())
        1 * favoriteRepository.findById(favoriteId) >> Optional.of(favorite)
        1 * favoriteRepository.delete(favorite)
    }

    def "deleteFavorite should throw NotFoundException if favorite does not exist"() {
        given: "a non-existent favorite ID"
        def email = "user@test.com"
        def favoriteId = 99L
        setupSecurityContext(email)

        when: "deleting a favorite"
        favoriteService.deleteFavorite(favoriteId)

        then: "NotFoundException is thrown"
        1 * usersRepository.findByEmail(email) >> Optional.of(new Users())
        1 * favoriteRepository.findById(favoriteId) >> Optional.empty()

        thrown(NotFoundException)
    }

    private void setupSecurityContext(String email) {
        def auth = Mock(Authentication)
        def context = Mock(SecurityContext)
        SecurityContextHolder.setContext(context)
        context.getAuthentication() >> auth
        auth.getName() >> email
    }
}