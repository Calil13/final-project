package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.*;
import org.example.finalproject.exception.BadRequestException;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.exception.WrongPasswordException;
import org.example.finalproject.mapper.UsersMapper;
import org.example.finalproject.repository.OtpRepository;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;
    private final OtpService otpService;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto getUserInfo(String email) {
        var user = usersRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found!");
                    return new NotFoundException("User not found!");
                });

        return usersMapper.toResponseDto(user);
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

        user.setPhone("+994" + updatePhone.getPhone());
        usersRepository.save(user);

        return "Phone updated successfully!";
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

        return "Email updated successfully!";
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

        usersRepository.delete(user);

        return "Account deleted!";
    }
}
