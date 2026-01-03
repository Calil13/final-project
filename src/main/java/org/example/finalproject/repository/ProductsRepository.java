package org.example.finalproject.repository;

import org.example.finalproject.entity.Orders;
import org.example.finalproject.entity.Payment;
import org.example.finalproject.entity.Products;
import org.example.finalproject.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductsRepository extends JpaRepository<Products, Long>, JpaSpecificationExecutor<Products> {

    Page<Products> findByOwnerId(Long ownerId, Pageable pageable);
    Page<Products> findByCategoryId(Long categoryId, Pageable pageable);
    Optional<Products> findByOwner(Users users);
}
