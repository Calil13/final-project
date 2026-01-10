package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.finalproject.dto.ProductReviewsResponseDto;
import org.example.finalproject.service.ProductReviewsService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("productReviews")
@Tag(name = "Product Reviews API", description = "Endpoints for creating, updating, deleting, and viewing product reviews")
public class ProductReviewsController {

    private final ProductReviewsService productReviewsService;

    @GetMapping("{productId}")
    public Page<ProductReviewsResponseDto> getReviews(
            @ParameterObject Pageable pageable,
            @PathVariable Long productId) {
        return productReviewsService.getReviews(pageable, productId);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("{productId}")
    public void addReviews(@PathVariable Long productId, String comment) {
        productReviewsService.addReviews(productId, comment);
    }
}
