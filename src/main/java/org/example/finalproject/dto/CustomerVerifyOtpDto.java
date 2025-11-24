package org.example.finalproject.dto;

import lombok.Data;

@Data
public class CustomerVerifyOtpDto {
    private String email;
    private String otp;
}