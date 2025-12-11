package org.example.finalproject.dto;

import lombok.Data;
import org.example.finalproject.validation.ValidEmail;

@Data
public class EmailVerifyOtpDto {
    @ValidEmail
    private String email;
    private String otp;
}