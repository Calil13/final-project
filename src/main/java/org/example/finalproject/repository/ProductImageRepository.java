package org.example.finalproject.repository;

import org.example.finalproject.dto.ProductImageDto;
import org.example.finalproject.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductId(Long productId);
}
