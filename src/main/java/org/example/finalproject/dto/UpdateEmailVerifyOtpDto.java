package org.example.finalproject.dto;

import org.example.finalproject.validation.ValidEmail;

public class UpdateEmailVerifyOtpDto {
    @ValidEmail
    private String newEmail;
    private String otp;
}
