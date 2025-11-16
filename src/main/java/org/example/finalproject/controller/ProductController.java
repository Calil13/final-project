package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.finalproject.dto.ProductResponseDto;
import org.example.finalproject.entity.Vendor;
import org.example.finalproject.service.ProductService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("final-project/product")
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Get all products",
            description = "This endpoint returns paginated list of all products. Supports pagination and sorting."
    )
    @GetMapping
    public Page<ProductResponseDto> getProducts(@ParameterObject Pageable pageable) {
        return productService.getProducts(pageable);
    }

    @Operation(
            summary = "Get products by vendor ID",
            description = "Returns paginated list of products belonging to a specific vendor."
    )
    @GetMapping("/{vendorId}")
    public Page<ProductResponseDto> getVendorProducts(@PathVariable Long vendorId, @ParameterObject Pageable pageable) {
        return productService.getVendorProducts(vendorId, pageable);
    }


}
