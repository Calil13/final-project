package org.example.finalproject.service;

import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.ProductImageDto;
import org.example.finalproject.entity.ProductImage;
import org.example.finalproject.entity.Products;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.repository.ProductImageRepository;
import org.example.finalproject.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductImageService {

    @Value("${upload.path}")
    private String uploadPath;

    private final ProductsRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public ProductImageService(ProductsRepository productRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
    }

    public List<ProductImageDto> getProductImages(Long productId) {
        List<ProductImage> productImage = productImageRepository.findByProductId(productId);

        if (productImage.isEmpty()) {
            log.error("Images not found for this product!");
            throw new NotFoundException("Images not found for this product!");
        }

        return productImage.stream()
                .map(img -> new ProductImageDto(img.getId(), img.getImageUrl()))
                .collect(Collectors.toList());
    }

    public List<ProductImageDto> uploadImage(Long productId, List<MultipartFile> files) {
        Products products = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found for this images!");
                    return new NotFoundException("Product not found!");
                });

        List<ProductImage> savedImages = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    String filePath = uploadPath + File.separator + fileName;
                    file.transferTo(new File(filePath));

                    ProductImage image = new ProductImage("/uploads/" + fileName, products);
                    savedImages.add(productImageRepository.save(image));
                } catch (IOException e) {
                    System.err.println("Error saving file: " + file.getOriginalFilename());
                    throw new RuntimeException("File upload failed: " + file.getOriginalFilename(), e);
                }
            }
        }

        return savedImages.stream()
                .map(img -> new ProductImageDto(img.getId(), img.getImageUrl()))
                .collect(Collectors.toList());
    }

    public String deleteImage(Long id) {
        ProductImage image = productImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Image not found!"));

        String filePath = uploadPath + File.separator
                + image.getImageUrl().replace("/uploads/", "");

        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }

        productImageRepository.delete(image);

        return "Image deleted successfully!";
    }
}
