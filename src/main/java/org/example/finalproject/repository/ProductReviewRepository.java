package org.example.finalproject.repository;

import org.example.finalproject.entity.ProductReview;
import org.example.finalproject.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    Page<ProductReview> findReviewsByProduct(Products product, Pageable pageable);
    Optional<ProductReview> findUserByProduct(Products product);
}
