package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.ProductResponseDto;
import org.example.finalproject.entity.Products;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductResponseDto> getProducts(Pageable pageable) {
        Page<Products> productsPage = productRepository.findAll(pageable);

        return productsPage.map(products -> {
            if (products.getCategory() == null) {
                log.error("Category not found!");
                throw  new NotFoundException("Category not found!");
            }

            if (products.getVendor() == null || products.getVendor().getUser().getName() == null) {
                log.error("Vendor or vendor user not found for product: {}", products.getId());
                throw  new NotFoundException("Vendor or vendor user not found for product: " + products.getId());
            }
            return ProductResponseDto.builder()
                    .id(products.getId())
                    .name(products.getName())
                    .description(products.getDescription())
                    .price(products.getPrice())
                    .stock(products.getStock())
                    .categoryName(products.getCategory().getName())
                    .vendorName(products.getVendor().getUser().getName())
                    .createdAt(products.getCreatedAt())
                    .build();
        });
    }
}
