package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.finalproject.dto.UpdateFullNameRequest;
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
    public UpdateFullNameRequest updateFullNameRequest(@RequestBody UpdateFullNameRequest update, Authentication authentication) {
        String email = authentication.getName();
        return usersService.updateFullNameRequest(update, email);
    }
}
