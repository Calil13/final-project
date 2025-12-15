package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.finalproject.dto.*;
import org.example.finalproject.service.UsersService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
@Tag(name = "Users API", description = "Product management endpoints")
public class UsersController {

    private final UsersService usersService;

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public UserResponseDto getUserInfo(Authentication authentication) {
        String email = authentication.getName();
        return usersService.getUserInfo(email);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/updateFullName")
    public UsersUpdateFullNameRequestDto updateFullNameRequest(@RequestBody UsersUpdateFullNameRequestDto update, Authentication authentication) {
        String email = authentication.getName();
        return usersService.updateFullNameRequest(update, email);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/newPhone")
    public String updatePhone(@Valid @RequestBody UsersUpdatePhoneDto updatePhone) {
        return usersService.updatePhone(updatePhone);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/newEmail/change-request")
    public String newEmailRequest(@Valid @RequestBody EmailSentOtpDto request, Authentication authentication) {
        String email = authentication.getName();
        return usersService.newEmailRequest(request, email);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/newEmail/verify")
    public String newEmailVerifyOtp(@Valid @RequestBody EmailVerifyOtpDto verifyOtp) {
        return usersService.newEmailVerifyOtp(verifyOtp);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/newPassword")
    public String updatePassword(@Valid @RequestBody UsersUpdatePasswordRequestDto updatePassword) {
        return usersService.updatePassword(updatePassword);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/delete")
    public String deleteAccount(@RequestBody UserCheckPassword checkPassword) {
        return usersService.deleteAccount(checkPassword);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/become")
    public String becomeOwner(@RequestBody OwnerRequestDto ownerRequestDTO) {
        return usersService.becomeOwner(ownerRequestDTO);
    }
}
