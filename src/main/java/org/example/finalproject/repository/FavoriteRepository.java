package org.example.finalproject.repository;

import org.example.finalproject.entity.Address;
import org.example.finalproject.entity.Favorites;
import org.example.finalproject.entity.Products;
import org.example.finalproject.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorites, Long> {
    Page<Favorites> findByCustomer(Users customer, Pageable pageable);
    Optional<Favorites> findByCustomer(Users users);
    boolean existsByCustomerAndProduct(Users customer, Products product);
}
