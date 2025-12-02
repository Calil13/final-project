package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.VendorRequestDTO;
import org.example.finalproject.entity.Customer;
import org.example.finalproject.entity.Payment;
import org.example.finalproject.entity.Users;
import org.example.finalproject.entity.Vendor;
import org.example.finalproject.enums.PaymentMethod;
import org.example.finalproject.enums.PaymentPurpose;
import org.example.finalproject.enums.PaymentStatus;
import org.example.finalproject.enums.UserRole;
import org.example.finalproject.exception.AlreadyExistsException;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.repository.CustomerRepository;
import org.example.finalproject.repository.PaymentRepository;
import org.example.finalproject.repository.UsersRepository;
import org.example.finalproject.repository.VendorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class VendorsService {

    private final CustomerRepository customerRepository;
    private final UsersRepository usersRepository;
    private final VendorRepository vendorRepository;
    private final PaymentRepository paymentRepository;

    public String becomeVendor(VendorRequestDTO dto) {
        //log.info("start method");

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> {
                    log.error("Customer not found!");
                    return new NotFoundException("Customer not found!");
                });

        Users user = customer.getUser();

        if (user.getUserRole() == UserRole.VENDOR) {
            log.error("User is already a vendor!");
            throw new AlreadyExistsException("User is already a vendor!");
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
                .purpose(PaymentPurpose.VENDOR_SUBSCRIPTION)
                .cardNumber(last4)
                .cvv(maskedCvv)
                .expireDate(dto.getExpireDate())
                .build();

        paymentRepository.save(payment);

        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        Vendor vendor = Vendor.builder()
                .user(user)
                .storeName(dto.getStoreName())
                .isActive(true)
                .build();

        vendorRepository.save(vendor);

        user.setUserRole(UserRole.VENDOR);
        usersRepository.save(user);

        Customer oldCustomer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow();

        customerRepository.delete(oldCustomer);

        return "Customer successfully became a VENDOR!";

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
