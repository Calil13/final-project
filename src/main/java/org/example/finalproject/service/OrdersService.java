package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.OrdersDto;
import org.example.finalproject.entity.Orders;
import org.example.finalproject.enums.OrderStatus;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.mapper.OrdersMapper;
import org.example.finalproject.repository.OrdersRepository;
import org.example.finalproject.repository.ProductsRepository;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final UsersRepository usersRepository;
    private final ProductsRepository productsRepository;
    private final OrdersMapper ordersMapper;

    public String createOrder(OrdersDto ordersDto) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        var product = productsRepository.findById(ordersDto.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        Double totalAmount = product.getPrice() * ordersDto.getDay();

        Orders order = Orders.builder()
                .customer(user)
                .product(product)
                .day(ordersDto.getDay())
                .total_amount(totalAmount)
                .orderStatus(OrderStatus.PENDING)
                .build();

        product.setIsAvailable(false);
        productsRepository.save(product);

        ordersRepository.save(order);

        return "Your total amount : " + totalAmount + " AZN";
    }
}
