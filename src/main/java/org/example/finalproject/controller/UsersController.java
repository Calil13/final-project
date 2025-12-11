package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.finalproject.dto.EmailStartDto;
import org.example.finalproject.dto.EmailVerifyOtpDto;
import org.example.finalproject.dto.UsersUpdateFullNameRequest;
import org.example.finalproject.dto.UserResponseDto;
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
    public UsersUpdateFullNameRequest updateFullNameRequest(@RequestBody UsersUpdateFullNameRequest update, Authentication authentication) {
        String email = authentication.getName();
        return usersService.updateFullNameRequest(update, email);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/email/change-request")
    public String newEmailRequest(@Valid @RequestBody EmailStartDto request, Authentication authentication) {
        String email = authentication.getName();
        return usersService.newEmailRequest(request, email);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/email/verify")
    public String newEmailVerifyOtp(@Valid @RequestBody EmailVerifyOtpDto verifyOtp) {
        return usersService.newEmailVerifyOtp(verifyOtp);
    }
}
