package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.PayCardDto;
import org.example.finalproject.dto.PaymentResponseDto;
import org.example.finalproject.entity.Address;
import org.example.finalproject.entity.Payment;
import org.example.finalproject.enums.*;
import org.example.finalproject.exception.IllegalArgumentException;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentsService {

    private final UsersRepository usersRepository;
    private final OrdersRepository ordersRepository;
    private final PaymentRepository paymentRepository;
    private final AddressRepository addressRepository;

    public PaymentResponseDto pay(PaymentMethod paymentMethod, PayCardDto cardDto) {
        return
                switch (paymentMethod) {
                    case CARD -> payByCard(cardDto);
                    case CASH -> payByCash();
                };
    }

    private PaymentResponseDto payByCard(PayCardDto cardDto) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var customer = usersRepository.findByEmailAndDeletedFalse(currentEmail)
                .orElseThrow(() -> {
                    log.error("Customer not found!");
                    return new NotFoundException("Customer not found!");
                });

        if (cardDto == null) {
            throw new IllegalArgumentException("Card payment requires card data!");
        }

        var order = ordersRepository.findByCustomerAndOrderStatus(customer, OrderStatus.CREATED)
                .orElseThrow(() -> {
                    log.error("Active order not found!");
                    return new NotFoundException("Active order not found!");
                });

        String cardNumber = cardDto.getCardNumber();
        String cvv = cardDto.getCvv();
        String expireDate = cardDto.getExpireDate();

        String validationError = validateCard(cardNumber, cvv, expireDate);

        if (validationError != null) {
            log.error("Payment FAILED! \n{}", validationError);
            return new PaymentResponseDto(
                    "Payment FAILED! " + validationError
            );
        }

        String last4 = cardDto.getCardNumber().substring(cardDto.getCardNumber().length() - 4);
        String maskedCvv = "***";

        if (!cardDto.getAmount().equals(order.getTotalAmount())) {
            log.error("The amount entered by the customer does not match the order total.");
            throw new IllegalArgumentException("Entered amount does not match order total");
        }

        Payment payment = Payment.builder()
                .user(customer)
                .order(order)
                .amount(cardDto.getAmount())
                .paymentDate(LocalDateTime.now())
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.SUCCESS)
                .purpose(PaymentPurpose.ORDER)
                .cardNumber(last4)
                .cvv(maskedCvv)
                .expireDate(cardDto.getExpireDate())
                .build();

        paymentRepository.save(payment);

        order.setOrderStatus(OrderStatus.PAID);
        ordersRepository.save(order);

        usersRepository.save(customer);

        if (order.getDeliveryType() == DeliveryType.PICKUP) {

            var product = order.getProduct();
            var owner = product.getOwner();

            Address address = addressRepository.findByUser(owner)
                    .orElseThrow(() -> {
                log.error("Pickup address not found for owner {}", owner.getEmail());
                return new NotFoundException("Pickup address not found");
            });

            String pickupAddress = address.getCity() + ", \n"
                    + address.getStreet() + ", \n"
                    + address.getHome();

            return new PaymentResponseDto("Payment completed successfully." + " - Owner address :" + pickupAddress);
        }

        return new PaymentResponseDto("Payment completed successfully.");
    }

    private PaymentResponseDto payByCash() {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var customer = usersRepository.findByEmailAndDeletedFalse(currentEmail)
                .orElseThrow(() -> {
                    log.error("Customer not found.");
                    return new NotFoundException("Customer not found!");
                });

        var order = ordersRepository.findByCustomerAndOrderStatus(customer, OrderStatus.CREATED)
                .orElseThrow(() -> {
                    log.error("Active order not found.");
                    return new NotFoundException("Active order not found!");
                });

        Payment payment = Payment.builder()
                .user(customer)
                .order(order)
                .amount(order.getTotalAmount())
                .paymentDate(LocalDateTime.now())
                .paymentMethod(PaymentMethod.CASH)
                .paymentStatus(PaymentStatus.PENDING)
                .purpose(PaymentPurpose.ORDER)
                .build();

        paymentRepository.save(payment);

        order.setOrderStatus(OrderStatus.PENDING);
        ordersRepository.save(order);

        usersRepository.save(customer);

        if (order.getDeliveryType() == DeliveryType.PICKUP) {

            var product = order.getProduct();
            var owner = product.getOwner();

            Address address = addressRepository.findByUser(owner)
                    .orElseThrow(() -> {
                        log.error("Pickup address not found for owner {}.", owner.getEmail());
                        return new NotFoundException("Pickup address not found");
                    });

            String pickupAddress = address.getCity() + ", \n"
                    + address.getStreet() + ", \n"
                    + address.getHome();

            return new PaymentResponseDto("Cash payment will be collected on delivery." + " - Owner address :" + pickupAddress);
        }

        return new PaymentResponseDto(
                "Cash payment will be collected on delivery."
        );
    }

    public String validateCard(String cardNumber, String cvv, String expireDate) {

        if (cardNumber == null || !cardNumber.matches("\\d{16}")) {
            return "Card number must be exactly 16 digits.";
        }

        if (cvv == null || !cvv.matches("\\d{3}")) {
            return "CVV must be exactly 3 digits.";
        }

        if (expireDate == null || !expireDate.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
            return "Expire date must be in MM/YY format.";
        }

        return null;
    }
}
