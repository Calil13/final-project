package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.ProductReviewsResponseDto;
import org.example.finalproject.entity.ProductReview;
import org.example.finalproject.exception.GlobalExceptionHandler;
import org.example.finalproject.exception.IllegalStateException;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.mapper.ProductReviewsMapper;
import org.example.finalproject.mapper.UsersMapper;
import org.example.finalproject.repository.ProductReviewRepository;
import org.example.finalproject.repository.ProductsRepository;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@Slf4j
@AllArgsConstructor
public class ProductReviewsService {

    private final UsersRepository usersRepository;
    private final ProductsRepository productsRepository;
    private final ProductReviewRepository productReviewRepository;
    private final ProductReviewsMapper productReviewsMapper;

    public Page<ProductReviewsResponseDto> getReviews(Pageable pageable, Long productId) {

        var product = productsRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", productId);
                    return new NotFoundException("Product not found!");
                });

        Page<ProductReview> reviews = productReviewRepository.findReviewsByProduct(product, pageable);

        log.info("Returned comment for product with id={}", product.getId());

        return reviews.map(review -> productReviewsMapper.toDto(review, review.getCustomer().getName()));
    }

    public void addReviews(Long productId, String comment) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var customer = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("Customer not found!"));

        var product = productsRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Customer not found!"));

        if (product.getIsAvailable().equals(false)) {
            log.error("A review cannot be added to an inactive product.");
            throw new IllegalStateException("A review cannot be added to an inactive product.");
        }

        ProductReview review = ProductReview.builder()
                .comment(comment)
                .product(product)
                .customer(customer)
                .build();

        log.info("Review added for product with ID: {}", productId);
        productReviewRepository.save(review);
    }

    public void editReviews(Long reviewId, String editedComment) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var customer = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("Customer not found!"));

        var review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found!");
                    return new NotFoundException("Review not found!");
                });

        if (!customer.getId().equals(review.getCustomer().getId())) {
            log.error("Unauthorized edit attempt. UserId: {}, ReviewId: {}",
                    customer.getId(), reviewId);
            throw new IllegalStateException("You are not allowed to edit this review!");
        }

        review.setComment(editedComment);
        productReviewRepository.save(review);
    }

    public String deleteReview(Long reviewId) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var customer = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("Customer not found!"));

        var review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found.");
                    return new NotFoundException("Review not found!");
                });

        if (!customer.getId().equals(review.getCustomer().getId())) {
            log.error("Unauthorized delete attempt. UserId: {}, ReviewId: {}",
                    customer.getId(), reviewId);
            throw new IllegalStateException("You are not allowed to edit this review!");
        }

        productReviewRepository.delete(review);

        log.warn("Review deleted for ID: {}", reviewId);
        return "Review deleted.";
    }
}
