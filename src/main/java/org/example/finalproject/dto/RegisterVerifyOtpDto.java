package org.example.finalproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.finalproject.validation.ValidEmail;

@Data
public class RegisterVerifyOtpDto {
    @ValidEmail
    private String email;
    private String otp;
}