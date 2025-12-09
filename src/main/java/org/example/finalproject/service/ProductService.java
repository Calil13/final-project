package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.ProductRequestDto;
import org.example.finalproject.dto.ProductResponseDto;
import org.example.finalproject.entity.Category;
import org.example.finalproject.entity.Products;
import org.example.finalproject.entity.Vendor;
import org.example.finalproject.exception.AccessDeniedException;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.mapper.ProductsMapper;
import org.example.finalproject.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;

    public Page<ProductResponseDto> getProducts(Pageable pageable) {
        Page<Products> productsPage = productRepository.findAll(pageable);
        return productsPage.map(productsMapper::toDto);
    }

    public ProductResponseDto getProduct(Long id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", id);
                    return new NotFoundException("Product not found!");
                });

        return productsMapper.toDto(product);
    }

    public Page<ProductResponseDto> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(productsMapper::toDto);
    }


    public Page<ProductResponseDto> getVendorProducts(Long vendorId, Pageable pageable) {

        Page<Products> products = productRepository.findByVendorId(vendorId, pageable);

        if (products.isEmpty()) {
            log.warn("Vendor has no products: {}", vendorId);
        }

        return products.map(productsMapper::toDto);
    }

    public ProductRequestDto addProduct(ProductRequestDto requestDto) {

        Vendor vendor = vendorRepository.findById(requestDto.getVendorId())
                .orElseThrow(() -> {
                    log.error("Vendor not found with id: {}", requestDto.getVendorId());
                    return new NotFoundException("Vendor not found");
                });

        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> {
                    log.error("Category not found with id: {}", requestDto.getCategoryId());
                    return new NotFoundException("Category not found");
                });

        Products products = productsMapper.toEntity(requestDto, vendor, category);

        productRepository.save(products);

        return productsMapper.toDtoRequest(products);
    }

    public ProductRequestDto editProduct(ProductRequestDto requestDto) {
        var product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> {
                    log.error("Product not found: {}", requestDto.getProductId());
                    return new NotFoundException("Product not found");
                });

        if(!product.getVendor().getId().equals(requestDto.getVendorId())) {
            throw new AccessDeniedException("You can only edit your own products");
        }

        var category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> {
                    log.error("Category not found: {}", requestDto.getCategoryId());
                    return new  NotFoundException("Category not found");
                });

        if (requestDto.getCategoryId() != null) {
            product.setCategory(category);
        }

        if (requestDto.getName() != null) {
            product.setName(requestDto.getName());
        }

        if (requestDto.getDescription() != null) {
            product.setDescription(requestDto.getDescription());
        }

        if (requestDto.getPrice() != null) {
            product.setPrice(requestDto.getPrice());
        }

        productRepository.save(product);

        return productsMapper.toDtoRequest(product);
    }
}
