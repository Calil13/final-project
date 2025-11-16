package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.ProductResponseDto;
import org.example.finalproject.entity.Products;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.mapper.ProductsMapper;
import org.example.finalproject.repository.ProductRepository;
import org.example.finalproject.repository.VendorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductsMapper productsMapper;
    private final VendorRepository vendorRepository;

    public Page<ProductResponseDto> getProducts(Pageable pageable) {

        Page<Products> productsPage = productRepository.findAll(pageable);

        return productsPage.map(products -> {
            if (products.getCategory() == null) {
                log.error("Category not found!");
                throw new NotFoundException("Category not found!");
            }

            if (products.getVendor() == null || products.getVendor().getUser().getName() == null) {
                log.error("Vendor not found for product: {}", products.getId());
                throw new NotFoundException("Vendor or vendor user not found for product: " + products.getId());
            }

            return productsMapper.toDto(products);
        });
    }

    public Page<ProductResponseDto> getVendorProducts(Long vendorId, Pageable pageable) {

        vendorRepository.findById(vendorId)
                .orElseThrow(() -> {
                    log.error("Vendor not found with id: {}", vendorId);
                    return new NotFoundException("Vendor not found!");
                });

        Page<Products> products = productRepository.findByVendorId(vendorId, pageable);

        if (products.isEmpty()) {
            log.warn("Vendor has no products: {}" , vendorId);
        }

        return products.map(productsMapper::toDto);
    }
}
