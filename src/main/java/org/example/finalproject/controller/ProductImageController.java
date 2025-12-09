package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.finalproject.dto.ProductImageDto;
import org.example.finalproject.entity.ProductImage;
import org.example.finalproject.service.ProductImageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/productImage")
@Tag(name = "Images API", description = "Products' images endpoints.")
public class ProductImageController {

    private final ProductImageService productImageService;

    @GetMapping("/{productId}")
    public List<ProductImageDto> getProductImages(@PathVariable Long productId) {
        return productImageService.getProductImages(productId);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(value = "{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<ProductImageDto> uploadImage(@PathVariable Long productId, @RequestPart("files") List<MultipartFile> files) {
        return productImageService.uploadImage(productId, files);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public String  deleteImage(@PathVariable Long id) {
        return productImageService.deleteImage(id);
    }
}
