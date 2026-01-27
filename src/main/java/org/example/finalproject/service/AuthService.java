package org.example.finalproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.*;
import org.example.finalproject.entity.Address;
import org.example.finalproject.entity.RefreshToken;
import org.example.finalproject.entity.Users;
import org.example.finalproject.enums.UserRole;
import org.example.finalproject.exception.*;
import org.example.finalproject.jwt.JwtUtil;
import org.example.finalproject.mapper.AddressMapper;
import org.example.finalproject.mapper.UsersMapper;
import org.example.finalproject.repository.AddressRepository;
import org.example.finalproject.repository.RefreshTokenRepository;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UsersMapper customerMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public String startRegistration(EmailSentOtpDto start) {

        if (usersRepository.findByEmail(start.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Email is already in use!");
        }

        return otpService.sendOtp(start.getEmail());
    }

    public String verifyOtp(EmailVerifyOtpDto verify) {
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
        user.setPhone("+994" + finish.getPhone());
        user.setDeleted(false);

        Address address = addressMapper.toEntity(finish.getAddress());
        address.setUser(user);

        usersRepository.save(user);
        addressRepository.save(address);

        otpService.removeOtp(finish.getEmail());

        return "Customer successfully registered!";
    }


    public AuthResponseDto refreshToken(RefreshTokenRequestDto request) {
        String oldRefreshTokenStr = request.getRefreshToken();

        RefreshToken storedToken = refreshTokenRepository.findByToken(oldRefreshTokenStr)
                .orElseThrow(() -> new NotFoundException("Refresh token not found!"));

        if (storedToken.isRevoked() || storedToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new NotValidException("Refresh token is invalid or expired!");
        }

        Users user = storedToken.getUser();
        String email = user.getEmail();

        refreshTokenRepository.delete(storedToken);

        String newAccessToken = jwtUtil.generateAccessToken(email);
        String newRefreshTokenStr = jwtUtil.generateRefreshToken();

        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(newRefreshTokenStr)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(30))
                .revoked(false)
                .build();

        refreshTokenRepository.save(newRefreshToken);

        return new AuthResponseDto(newAccessToken, newRefreshTokenStr);
    }

    public String forgotPassword(EmailSentOtpDto sentOpt) {

        var user = usersRepository.findByEmail(sentOpt.getEmail())
                .orElseThrow(() -> new NotFoundException("Email not found!"));

        return otpService.sendOtp(sentOpt.getEmail());
    }

    public String verifyEmail(EmailVerifyOtpDto verifyOtp) {
        otpService.verifyOtp(verifyOtp.getEmail(), verifyOtp.getOtp());
        return "OTP verified!";
    }

    public String resetPassword(UsersForgetPasswordDto forgetPassword) {

        var user = usersRepository.findByEmail(forgetPassword.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found!"));

        if (!otpService.isVerified(forgetPassword.getEmail())) {
            throw new NotValidException("OTP is not verified!");
        }

        if (!forgetPassword.getNewPassword().equals(forgetPassword.getConfirmNewPassword())) {
            throw new WrongPasswordException("Write the new code in the same way!");
        }

        if (passwordEncoder.matches(forgetPassword.getNewPassword(), user.getPassword())) {
            throw new WrongPasswordException("New password cannot be same as the old password!");
        }

        user.setPassword(passwordEncoder.encode(forgetPassword.getNewPassword()));
        usersRepository.save(user);

        otpService.removeOtp(user.getEmail());

        return "Password updated successfully!";
    }

    @Transactional
    public AuthResponseDto login(String email, String password) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found. Please register first."));

        if(user.getUserRole() == UserRole.ADMIN) {
            log.error("Admin must log in using the 'admin/login' method.");
            throw new AccessDeniedException("LOGIN_ERROR");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new WrongPasswordException("Password is Wrong!");
        }

        refreshTokenRepository.deleteByUser(user);

        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshTokenStr = jwtUtil.generateRefreshToken();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .user(user)
                .expiryDate(LocalDateTime.now().plusDays(30))
                .revoked(false)
                .build();

        user.setIsActive(true);
        refreshTokenRepository.save(refreshToken);

        return new AuthResponseDto(accessToken, refreshTokenStr);
    }

    @Transactional
    public AuthResponseDto adminLogin(String email, String password) {

        var user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Admin not found!"));

        if (user.getUserRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("You are not admin!");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new WrongPasswordException("Password is wrong!");
        }

        String adminAccessToken = jwtUtil.generateAdminAccessToken(email);

        user.setIsActive(true);
        usersRepository.save(user);

        return new AuthResponseDto(adminAccessToken, null);
    }

    @Transactional
    public String logout(LogoutRequestDto logoutRequest) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        if (!passwordEncoder.matches(logoutRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Password is incorrect");
        }

        var token = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Refresh token not found!"));

        refreshTokenRepository.deleteByToken(token.getToken());
        user.setIsActive(false);

        return "Logged out successfully!";
    }
}

