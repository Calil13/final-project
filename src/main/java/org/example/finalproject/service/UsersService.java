package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.*;
import org.example.finalproject.entity.Payment;
import org.example.finalproject.enums.PaymentMethod;
import org.example.finalproject.enums.PaymentPurpose;
import org.example.finalproject.enums.PaymentStatus;
import org.example.finalproject.enums.UserRole;
import org.example.finalproject.exception.*;
import org.example.finalproject.mapper.AddressMapper;
import org.example.finalproject.mapper.UsersMapper;
import org.example.finalproject.repository.AddressRepository;
import org.example.finalproject.repository.PaymentRepository;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final PaymentRepository paymentRepository;
    private final PaymentsService paymentsService;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @SuppressWarnings("LoggingSimilarMessage")
    public UserResponseDto getUserInfo() {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> {
                    log.error("User not found.");
                    return new NotFoundException("User not found!");
                });

        var address = addressRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.error("Address not found for user ID: {}", user.getId());
                    return new NotFoundException("Address not found!");
                });

        var addressDto = addressMapper.toDto(address);

        log.info("User's information returned.");
        return usersMapper.toResponseDto(user, addressDto);
    }

    public UserResponsePublicDto getUserInfoPublic(Long id) {
        var user = usersRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found!");
                    return new NotFoundException("User not found!");
                });

        if (user.getUserRole().equals(UserRole.ADMIN)) {
            log.error("An exception was thrown because the ADMIN id was entered.");
            throw new NotFoundException("User not found!");
        } else if (user.getDeleted().equals(true)) {
            log.error("Requested to return deleted account. Account ID: {}", id);
            throw new NotFoundException("User not found!");
        }

        var address = addressRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.error("Address not found for user ID: {}", user.getId());
                    return new NotFoundException("Address not found!");
                });

        var addressDto = addressMapper.toDto(address);

        log.info("User's information returned for customer.");
        return usersMapper.toResponsePublicDto(user, addressDto);
    }

    public UsersUpdateFullNameRequestDto updateFullNameRequest(UsersUpdateFullNameRequestDto update, String email) {
        var user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        user.setName(update.getName());
        user.setSurname(update.getSurname());

        usersRepository.save(user);

        return usersMapper.toFullNameDto(user);
    }

    public String updatePhone(UsersUpdatePhoneDto updatePhone) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        user.setPhone("+994" + updatePhone.getNewPhone());
        usersRepository.save(user);

        log.info("User's phone updated successfully.");
        return "Phone updated successfully.";
    }

    public String newEmailRequest(EmailSentOtpDto request, String email) {
        var users = usersRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Email not found!");
                    return new NotFoundException("Email not found!");
                });


        if (users.getEmail().equals(request.getEmail())) {
            throw new BadRequestException("New email cannot be the same as current email");
        }

        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already used");
        }

        otpService.sendOtp(request.getEmail());

        return "OTP sent successfully!";
    }

    public String newEmailVerifyOtp(EmailVerifyOtpDto verify) {

        otpService.verifyOtp(verify.getEmail(), verify.getOtp());

        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        user.setEmail(verify.getEmail());
        usersRepository.save(user);

        otpService.removeOtp(verify.getEmail());

        log.info("User's email updated successfully.");
        return "Email updated successfully.";
    }

    public String updatePassword(UsersUpdatePasswordRequestDto updatePassword) {

        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        if (!passwordEncoder.matches(updatePassword.getCurrentPassword(), user.getPassword())) {
            throw new WrongPasswordException("Existing Password is wrong!");
        }

        if (!updatePassword.getNewPassword().equals(updatePassword.getConfirmNewPassword())) {
            throw new WrongPasswordException("Confirm new password correctly!");
        }

        if (passwordEncoder.matches(updatePassword.getNewPassword(), user.getPassword())) {
            throw new WrongPasswordException("New password cannot be same as the existing password!");
        }

        user.setPassword(passwordEncoder.encode(updatePassword.getNewPassword()));
        usersRepository.save(user);

        return "Password changed successfully!";
    }

    public String deleteAccount(UserCheckPassword checkPassword) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        if (!passwordEncoder.matches(checkPassword.getPassword(), user.getPassword())) {
            throw new WrongPasswordException("Enter the correct password!");
        }

        if (user.getDeleted().equals(true)) {
            log.error(String.valueOf(new AlreadyDeletedException("User delete column already is true!")));
            throw new AlreadyDeletedException("User already deleted!");
        }

        user.setIsActive(false);
        user.setDeleted(true);
        usersRepository.save(user);

        log.info("Account deleted.");
        return "Account deleted.";
    }

    public String becomeOwner(OwnerRequestDto dto) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var customer = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> {
                    log.error("Customer not found!");
                    return new NotFoundException("Customer not found!");
                });

        if (customer.getUserRole() == UserRole.OWNER) {
            log.error("User is already a owner!");
            throw new AlreadyExistsException("User is already a owner!");
        }

        String cardNumber = dto.getCardNumber();
        String cvv = dto.getCvv();
        String expireDate = dto.getExpireDate();

        String validationError = paymentsService.validateCard(cardNumber, cvv, expireDate);

        if (validationError != null) {
            log.error("Payment failed for becoming an owner. \n{}", validationError);
            return "Payment FAILED! \n" + validationError;
        }

        String last4 = dto.getCardNumber().substring(dto.getCardNumber().length() - 4);
        String maskedCvv = "***";

        Payment payment = Payment.builder()
                .user(customer)
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

        customer.setUserRole(UserRole.OWNER);
        usersRepository.save(customer);

        log.info("Customer ID {}: Status updated to OWNER successfully.", customer.getId());
        return "Customer successfully became a OWNER.";

    }
}
