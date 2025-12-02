package org.example.finalproject.service;

import lombok.RequiredArgsConstructor;
import org.example.finalproject.dto.*;
import org.example.finalproject.entity.Customer;
import org.example.finalproject.entity.RefreshToken;
import org.example.finalproject.entity.Users;
import org.example.finalproject.enums.UserRole;
import org.example.finalproject.exception.AlreadyExistsException;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.exception.NotValidException;
import org.example.finalproject.exception.WrongPasswordException;
import org.example.finalproject.jwt.JwtUtil;
import org.example.finalproject.mapper.UsersMapper;
import org.example.finalproject.repository.CustomerRepository;
import org.example.finalproject.repository.RefreshTokenRepository;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final CustomerRepository customerRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UsersMapper customerMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;

    public String startRegistration(RegisterStartDto start) {

        if (usersRepository.findByEmail(start.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Email is already in use!");
        }

        return otpService.sendOtp(start.getEmail());
    }

    public String verifyOtp(RegisterVerifyOtpDto verify) {
        otpService.verifyOtp(verify.getEmail(), verify.getOtp());
        return "OTP verified!";
    }

    public String finishRegistration(RegisterFinishDto finish) {

        if (!otpService.isVerified(finish.getEmail())) {
            throw new NotValidException("OTP not verified!");
        }

        Users user = customerMapper.toEntity(finish);
        user.setPassword(passwordEncoder.encode(finish.getPassword()));
        user.setUserRole(UserRole.CUSTOMER);
        usersRepository.save(user);

        Customer customer = Customer.builder()
                .user(user)
                .build();
        customerRepository.save(customer);

        otpService.removeOtp(finish.getEmail());

        return "Customer successfully registered!";
    }

    public AuthResponseDto login(String email, String password) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new WrongPasswordException("Password is Wrong!");
        }

        refreshTokenRepository.deleteByUser(user);

        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshTokenStr = jwtUtil.generateRefreshToken(email);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(30))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return new AuthResponseDto(accessToken, refreshTokenStr);
    }

    public AuthResponseDto refreshToken(RefreshTokenRequestDto request) {
        String oldRefreshTokenStr = request.getRefreshToken();

        RefreshToken storedToken = refreshTokenRepository.findByToken(oldRefreshTokenStr)
                .orElseThrow(() -> new NotFoundException("Refresh token not found"));

        if (storedToken.isRevoked() || storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new NotValidException("Refresh token is invalid or expired");
        }

        String email = jwtUtil.extractEmail(oldRefreshTokenStr);

        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

       refreshTokenRepository.delete(storedToken);

        String newAccessToken = jwtUtil.generateAccessToken(email);
        String newRefreshTokenStr = jwtUtil.generateRefreshToken(email);

        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(newRefreshTokenStr)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(30))
                .revoked(false)
                .build();

        refreshTokenRepository.save(newRefreshToken);

        return new AuthResponseDto(newAccessToken, newRefreshTokenStr);
    }
}

