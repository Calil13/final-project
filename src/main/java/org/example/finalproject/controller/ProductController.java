package org.example.finalproject.controller;

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
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Page<ProductResponseDto> getProducts(@ParameterObject Pageable pageable) {
        return productService.getProducts(pageable);
    }

    @GetMapping("/{vendorId}")
    public Page<ProductResponseDto> getVendorProducts(@PathVariable Long vendorId, @ParameterObject Pageable pageable) {
        return productService.getVendorProducts(vendorId, pageable);
    }
}
