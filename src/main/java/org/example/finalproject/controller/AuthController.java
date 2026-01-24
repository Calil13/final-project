package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.finalproject.dto.*;
import org.example.finalproject.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "Registration and Login endpoints")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Start registration process",
            description = "User enters email and receives an OTP code"
    )
    @PostMapping("/register/start")
    public String startRegistration(@Valid @RequestBody EmailSentOtpDto startDto) {
        return authService.startRegistration(startDto);
    }

    @Operation(
            summary = "Verify OTP code",
            description = "Verifies the OTP sent to the user's email"
    )
    @PostMapping("/register/verify")
    public String verifyOtp(@Valid @RequestBody EmailVerifyOtpDto verifyOtpDto) {
        return authService.verifyOtp(verifyOtpDto);
    }

    @Operation(
            summary = "Complete registration",
            description = "After verifying OTP, the user enters the data"
    )
    @PostMapping("/register/finish")
    public String finishRegistration(@Valid @RequestBody RegisterFinishDto finishDto) {
        return authService.finishRegistration(finishDto);
    }

    @PostMapping("/refreshToken")
    public AuthResponseDto refresh(@RequestBody RefreshTokenRequestDto request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/password/forgot")
    public String forgotPassword(@Valid @RequestBody EmailSentOtpDto sentOtp) {
        return authService.forgotPassword(sentOtp);
    }

    @PostMapping("/password/verify")
    public String verifyEmail(@Valid @RequestBody EmailVerifyOtpDto verifyOtp) {
        return authService.verifyEmail(verifyOtp);
    }

    @PostMapping("/password/reset")
    public String resetPassword(@Valid @RequestBody UsersForgetPasswordDto forgetPassword) {
        return authService.resetPassword(forgetPassword);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        return authService.login(request.getEmail(), request.getPassword());
    }

    @PostMapping("/admin/login")
    public AuthResponseDto adminLogin(@Valid @RequestBody AdminLoginRequestDto request) {
        return authService.adminLogin(request.getEmail(), request.getPassword()
        );
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    public String logout( @Valid @RequestBody LogoutRequestDto request) {
        return authService.logout(request);
    }
}

