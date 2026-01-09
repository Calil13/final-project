package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.FavoriteProductsDto;
import org.example.finalproject.entity.Favorites;
import org.example.finalproject.exception.AlreadyExistsException;
import org.example.finalproject.exception.IllegalStateException;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.mapper.FavoritesMapper;
import org.example.finalproject.repository.FavoriteRepository;
import org.example.finalproject.repository.ProductsRepository;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class FavoriteService {

    private final UsersRepository usersRepository;
    private final FavoriteRepository favoriteRepository;
    private final FavoritesMapper favoritesMapper;
    private final ProductsRepository productsRepository;

    public Page<FavoriteProductsDto> getFavorites(Pageable pageable) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var customer = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("Customer not found!"));

        Page<Favorites> favorites = favoriteRepository.findByCustomer(customer, pageable);

        return favorites.map(favoritesMapper::toDto);
    }

    public void addFavorite(Long productId) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var customer = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("Customer not found!"));

        var product = productsRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found!"));

        var existingFavorite = favoriteRepository
                .existsByCustomerAndProduct(customer, product);

        if (existingFavorite) {
            log.error("Product already added to favorites.");
            throw new AlreadyExistsException("Product already added to favorites.");
        }

        if (product.getIsAvailable().equals(false)) {
            log.error("Inactive product cannot be added to favorites.");
            throw new IllegalStateException("Inactive product cannot be added to favorites.");
        }

        Favorites favorites = Favorites.builder()
                .customer(customer)
                .product(product)
                .build();

        favoriteRepository.save(favorites);
    }

    public void deleteFavorite(Long favoriteId) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("Customer not found!"));

        var favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new NotFoundException("Favorite not found!"));

        favoriteRepository.delete(favorite);
    }
}
