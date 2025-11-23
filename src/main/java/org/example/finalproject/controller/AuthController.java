package org.example.finalproject.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.finalproject.dto.CustomerFinishRegisterDto;
import org.example.finalproject.dto.CustomerStartDto;
import org.example.finalproject.dto.CustomerVerifyOtpDto;
import org.example.finalproject.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("final-project/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/customer/start")
    public String startRegistration(@RequestBody CustomerStartDto customer) {
        return authService.startRegistration(customer);
    }

    @PostMapping("/register/customer/verify")
    public String verifyOtp(@RequestBody CustomerVerifyOtpDto dto) {
        return authService.verifyOtp(dto);
    }

    @PostMapping("/register/customer/finish")
    public String finishRegistration(@RequestBody CustomerFinishRegisterDto dto) {
        return authService.finishRegistration(dto);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request.getEmail(), request.getPassword());
    }

    @Data
    static class LoginRequest {
        private String email;
        private String password;
    }
}

