package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.finalproject.dto.ProductCreateDto;
import org.example.finalproject.dto.ProductUpdateDto;
import org.example.finalproject.dto.ProductResponseDto;
import org.example.finalproject.service.ProductsService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/products")
@Tag(name = "Products API", description = "Product management endpoints")
public class ProductController {

    private final ProductsService productService;

    @Operation(
            summary = "Get all products",
            description = "This endpoint returns paginated list of all products. Supports pagination and sorting."
    )
    @GetMapping
    public Page<ProductResponseDto> getProducts(@ParameterObject Pageable pageable) {
        return productService.getProducts(pageable);
    }

    @Operation(
            summary = "Get one products",
            description = "This endpoint returns one product for id."
    )
    @GetMapping("/{id}")
    public ProductResponseDto getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @Operation(
            summary = "Get products by category ID",
            description = "Returns paginated list of products belonging to a specific category."
    )
    @GetMapping("/categories/{id}")
    public Page<ProductResponseDto> getProductsByCategory(@PathVariable Long id, @ParameterObject Pageable pageable) {
        return productService.getProductsByCategory(id, pageable);
    }

    @Operation(
            summary = "Get products by owner ID",
            description = "Returns paginated list of products belonging to a specific owner."
    )
    @GetMapping("/owner/{ownerId}")
    public Page<ProductResponseDto> getOwnerProducts(@PathVariable Long ownerId, @ParameterObject Pageable pageable) {
        return productService.getOwnerProducts(ownerId, pageable);
    }

    @Operation(
            summary = "Add new product",
            description = "Creates a new product for the authenticated OWNER user. " +
                    "Only users with OWNER role are allowed to add products."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ProductCreateDto addProduct(@Valid @RequestBody ProductCreateDto createDto) {
        return productService.addProduct(createDto);
    }

    @Operation(
            summary = "Edit existing product",
            description = "Updates an existing product owned by the authenticated OWNER user. " +
                    "Only the product owner is allowed to modify product details."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping
    public ProductUpdateDto editProduct(@Valid @RequestBody ProductUpdateDto updateDto) {
        return productService.editProduct(updateDto);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public String deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }
}
