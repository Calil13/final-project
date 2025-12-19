package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.ProductRequestDto;
import org.example.finalproject.dto.ProductResponseDto;
import org.example.finalproject.entity.Category;
import org.example.finalproject.entity.Products;
import org.example.finalproject.entity.Users;
import org.example.finalproject.exception.AccessDeniedException;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.mapper.ProductsMapper;
import org.example.finalproject.repository.CategoryRepository;
import org.example.finalproject.repository.ProductsRepository;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ProductsService {

    private final ProductsRepository productRepository;
    private final ProductsMapper productsMapper;
    private final CategoryRepository categoryRepository;
    private final UsersRepository usersRepository;

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

    public Page<ProductResponseDto> getOwnerProducts(Long ownerId, Pageable pageable) {

        Page<Products> products = productRepository.findByOwnerId(ownerId, pageable);

        if (products.isEmpty()) {
            log.warn("Owner has no products: {}", ownerId);
        }

        return products.map(productsMapper::toDto);
    }

    public ProductRequestDto addProduct(ProductRequestDto requestDto) {

        Users owner = usersRepository.findById(requestDto.getOwnerId())
                .orElseThrow(() -> {
                    log.error("Owner not found with id: {}", requestDto.getOwnerId());
                    return new NotFoundException("Owner not found");
                });

        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> {
                    log.error("Category not found with id: {}", requestDto.getCategoryId());
                    return new NotFoundException("Category not found");
                });

        Products products = productsMapper.toEntity(requestDto, owner, category);

        productRepository.save(products);

        return productsMapper.toDtoRequest(products);
    }

    public ProductRequestDto editProduct(ProductRequestDto requestDto) {

        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        var product = productRepository.findByOwner(user)
                .orElseThrow(() -> {
                    log.error("Product not found!");
                    return new NotFoundException("Product not found!");
                });

        if(!product.getOwner().getId().equals(requestDto.getOwnerId())) {
            throw new AccessDeniedException("You can only edit your own products");
        }

        var category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> {
                    log.error("Category not found: {}", requestDto.getCategoryId());
                    return new  NotFoundException("Category not found!");
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

    public String deleteProduct(Long id) {

        String vendorEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmail(vendorEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        var product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found!"));

        if (!product.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not allowed to delete this product!");
        }

        productRepository.delete(product);

        return "Product deleted successfully!";
    }
}
