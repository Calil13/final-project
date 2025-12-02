package org.example.finalproject.controller;

import lombok.AllArgsConstructor;
import org.example.finalproject.entity.ProductImage;
import org.example.finalproject.service.ProductImageService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/productImage")
public class ProductImageController {

    private final ProductImageService productImageService;

    @GetMapping("/{productId}")
    public List<ProductImage> getProductImages(@PathVariable Long productId) {
        return productImageService.getProductImages(productId);
    }

    @PostMapping("{productId}")
    public List<ProductImage> uploadImage(@PathVariable Long productId, @RequestParam("files") List<MultipartFile> files) {
        return productImageService.uploadImage(productId, files);
    }
}
