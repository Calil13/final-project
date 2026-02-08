package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.ProductReviewsResponseDto;
import org.example.finalproject.entity.ProductReview;
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
    private final UsersMapper usersMapper;
    private final ProductsRepository productsRepository;
    private final ProductReviewRepository productReviewRepository;
    private final ProductReviewsMapper productReviewsMapper;

    public Page<ProductReviewsResponseDto> getReviews(Pageable pageable, Long productId) {

        var product = productsRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found!");
                    return new NotFoundException("Product not found!");
                });

        var productReview = productReviewRepository.findUserByProduct(product)
                .orElseThrow(() -> {
                    log.error("Review not found for product with id={}", productId);
                    return new NotFoundException("Review not found!");
                });

        Page<ProductReview> reviews = productReviewRepository.findReviewsByProduct(product, pageable);

        log.info("Returned comment with id={} for product with id={}", productReview.getId(), product);

        return reviews.map(review -> productReviewsMapper.toDto(review, productReview.getCustomer().getName()));
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

        productReviewRepository.save(review);
    }

    public void editReviews(Long reviewId, String editedComment) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("Customer not found!"));

        var review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found!");
                    return new NotFoundException("Review not found!");
                });

        review.setComment(editedComment);
        productReviewRepository.save(review);
    }

    public String deleteReview(Long reviewId) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("Customer not found!"));

        var review = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found.");
                    return new NotFoundException("Review not found!");
                });

        productReviewRepository.delete(review);

        return "Review deleted.";
    }
}
