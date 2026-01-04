package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.AddressDto;
import org.example.finalproject.dto.OrderInfoResponseDto;
import org.example.finalproject.dto.OrdersDto;
import org.example.finalproject.entity.Orders;
import org.example.finalproject.entity.Products;
import org.example.finalproject.entity.Users;
import org.example.finalproject.enums.DeliveryType;
import org.example.finalproject.enums.OrderStatus;
import org.example.finalproject.enums.PaymentMethod;
import org.example.finalproject.enums.PaymentStatus;
import org.example.finalproject.exception.AccessDeniedException;
import org.example.finalproject.exception.AlreadyExistsException;
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

    public OrderInfoResponseDto getDeliveryInfo() {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        var address = addressRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Address not found!"));

        AddressDto addressDto = addressMapper.toDto(address);

        return new OrderInfoResponseDto(user.getPhone(), addressDto);
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

    public void received(Long orderId) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        var order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found!"));

        var payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new NotFoundException("Payment not found!"));

        if (payment.getPaymentMethod().equals(PaymentMethod.CASH)) {
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
        }

        if (order.getOrderStatus().equals(OrderStatus.DELIVERED)) {
            throw new AlreadyExistsException("The order has already been delivered!");
        }

        order.setOrderStatus(OrderStatus.DELIVERED);
        ordersRepository.save(order);
    }

    public ResponseEntity<String> returnRental(Long orderId) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        var order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found!"));

        var product = order.getProduct();

        if (LocalDateTime.now().isBefore(order.getOrderDate())) {
            return ResponseEntity.badRequest()
                    .body("The product has not expired yet.");
        }

        product.setIsAvailable(true);
        productsRepository.save(product);

        order.setOrderStatus(OrderStatus.RETURNED);
        ordersRepository.save(order);

        return ResponseEntity.ok("The product has been returned.");
    }

    public String deleteOrder(Long productId) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("Customer not found!"));

        var product = productsRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found!"));

        var order = ordersRepository.findByProduct(product)
                .orElseThrow(() -> new NotFoundException("Order not found!"));

        product.setIsAvailable(true);

        productsRepository.save(product);
        ordersRepository.delete(order);

        return "The order has been cancelled.";
    }
}
