package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.finalproject.service.ProductReviewsService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("productReviews")
@Tag(name = "Product Reviews API", description = "Endpoints for creating, updating, deleting, and viewing product reviews")
public class ProductReviewsController {

    private final ProductReviewsService productReviewsService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("{productId}")
    public void addReviews(@PathVariable Long productId) {

    }
}
