package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.AddressDto;
import org.example.finalproject.dto.DeliveryInfoResponseDto;
import org.example.finalproject.dto.OrdersDto;
import org.example.finalproject.entity.Orders;
import org.example.finalproject.enums.DeliveryType;
import org.example.finalproject.enums.OrderStatus;
import org.example.finalproject.enums.PaymentStatus;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.exception.ProductNotAvailableException;
import org.example.finalproject.mapper.AddressMapper;
import org.example.finalproject.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;
    private final UsersRepository usersRepository;
    private final ProductsRepository productsRepository;
    private final AddressMapper addressMapper;
    private final AddressRepository addressRepository;
    private final PaymentRepository paymentRepository;

    public DeliveryInfoResponseDto getDeliveryInfo() {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        var address = addressRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Address not found!"));

        addressMapper.toDto(address);

        AddressDto addressDto = addressMapper.toDto(address);

        return new DeliveryInfoResponseDto(user.getPhone(), addressDto);
    }

    public String createOrder(DeliveryType deliveryType, OrdersDto ordersDto) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        var product = productsRepository.findById(ordersDto.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found!"));

        if (product.getIsAvailable().equals(false)) {
            log.error("Product is not available for ordering.");
            throw new ProductNotAvailableException("The product is already rented!");
        }

        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(ordersDto.getDay()));


        Orders order = Orders.builder()
                .customer(user)
                .product(product)
                .day(ordersDto.getDay())
                .totalAmount(totalAmount)
                .orderStatus(OrderStatus.CREATED)
                .deliveryType(deliveryType)
                .build();

        product.setIsAvailable(false);
        productsRepository.save(product);

        ordersRepository.save(order);

        return "Your total amount : " + totalAmount + " AZN";
    }

    public void received() {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        var payment = paymentRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Payment not found!"));

        var order = ordersRepository.findByCustomer(user)
                .orElseThrow(() -> new NotFoundException("Order not found!"));

        if (order.getDeliveryType().equals(DeliveryType.PICKUP)) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
        }

        order.setOrderStatus(OrderStatus.DELIVERED);
        ordersRepository.save(order);
    }

    public ResponseEntity<String> returnRental() {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        var order = ordersRepository.findByCustomer(user)
                .orElseThrow(() -> new NotFoundException("Order not found!"));

        if (LocalDateTime.now().isBefore(order.getOrderDate())) {
            return ResponseEntity.badRequest()
                    .body("The product has not expired yet.");
        }

        order.setOrderStatus(OrderStatus.RETURNED);
        ordersRepository.save(order);

        return ResponseEntity.ok("The product has been returned.");
    }
}
