package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.OwnerRequestDto;
import org.example.finalproject.entity.Customer;
import org.example.finalproject.entity.Payment;
import org.example.finalproject.entity.Users;
import org.example.finalproject.entity.Owner;
import org.example.finalproject.enums.PaymentMethod;
import org.example.finalproject.enums.PaymentPurpose;
import org.example.finalproject.enums.PaymentStatus;
import org.example.finalproject.enums.UserRole;
import org.example.finalproject.exception.AlreadyExistsException;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class OwnersService {

    private final CustomerRepository customerRepository;
    private final UsersRepository usersRepository;
    private final OwnerRepository vendorRepository;
    private final PaymentRepository paymentRepository;
    private final ProductsRepository productRepository;

    public String becomeOwner(OwnerRequestDto dto) {

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> {
                    log.error("Customer not found!");
                    return new NotFoundException("Customer not found!");
                });

        Users user = customer.getUser();

        if (user.getUserRole() == UserRole.OWNER) {
            log.error("User is already a owner!");
            throw new AlreadyExistsException("User is already a owner!");
        }

        String cardNumber = dto.getCardNumber();
        String cvv = dto.getCvv();
        String expireDate = dto.getExpireDate();

        String validationError = validateCard(cardNumber, cvv, expireDate);

        if (validationError != null) {
            return "Payment FAILED! \n" + validationError;
        }

        String last4 = dto.getCardNumber().substring(dto.getCardNumber().length() - 4);
        String maskedCvv = "***";

        Payment payment = Payment.builder()
                .user(user)
                .amount(dto.getAmount())
                .paymentDate(LocalDateTime.now())
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.PENDING)
                .purpose(PaymentPurpose.OWNER_SUBSCRIPTION)
                .cardNumber(last4)
                .cvv(maskedCvv)
                .expireDate(dto.getExpireDate())
                .build();

        paymentRepository.save(payment);

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        Owner vendor = Owner.builder()
                .user(user)
                .build();

        vendorRepository.save(vendor);

        user.setUserRole(UserRole.OWNER);
        usersRepository.save(user);

        Customer oldCustomer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow();

        customerRepository.delete(oldCustomer);

        return "Customer successfully became a OWNER!";

    }

    private String validateCard(String cardNumber, String cvv, String expireDate) {

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
