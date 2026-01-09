package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.exception.IllegalStateException;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.repository.ProductsRepository;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@Slf4j
@AllArgsConstructor
public class ProductReviewsService {

    private final UsersRepository usersRepository;
    private final ProductsRepository productsRepository;

    public void addReviews(Long productId) {
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


    }
}
