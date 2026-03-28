package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.example.finalproject.dto.*;
import org.example.finalproject.service.UsersService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Users API", description = "Product management endpoints")
public class UsersController {

    private final UsersService usersService;

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public UserResponseDto getUserInfo() {
        return usersService.getUserInfo();
    }

    @GetMapping("/public/{id}")
    public UserResponsePublicDto getUserInfoPublic(@PathVariable Long id) {
        return usersService.getUserInfoPublic(id);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/update-fullName")
    public UsersUpdateFullNameRequestDto updateFullNameRequest(@RequestBody UsersUpdateFullNameRequestDto update, Authentication authentication) {
        String email = authentication.getName();
        return usersService.updateFullNameRequest(update, email);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/update-phone")
    public String updatePhone(@Valid @RequestBody UsersUpdatePhoneDto updatePhone) {
        return usersService.updatePhone(updatePhone);
    }

    @Operation(
            summary = "Request email change",
            description = "Sends OTP to the new email address for verification."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/newEmail/change-request")
    public String newEmailRequest(@Valid @RequestBody EmailSentOtpDto request, Authentication authentication) {
        String email = authentication.getName();
        return usersService.newEmailRequest(request, email);
    }

    @Operation(
            summary = "Verify OTP and change email",
            description = "Verifies OTP sent to new email and updates user's email address."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/newEmail/verify")
    public String newEmailVerifyOtp(@Valid @RequestBody EmailVerifyOtpDto verifyOtp) {
        return usersService.newEmailVerifyOtp(verifyOtp);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/new-password")
    public String updatePassword(@Valid @RequestBody UsersUpdatePasswordRequestDto updatePassword) {
        return usersService.updatePassword(updatePassword);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/delete")
    public String deleteAccount(@RequestBody UserCheckPassword checkPassword) {
        return usersService.deleteAccount(checkPassword);
    }

    @Operation(
            summary = "Become owner",
            description = "Changes the user role from customer to owner."
    )
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/become-owner")
    public String becomeOwner(@Valid @RequestBody OwnerRequestDto ownerRequestDTO) {
        return usersService.becomeOwner(ownerRequestDTO);
    }
}
