package org.example.finalproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentPickupResponseDto {
    private String message;
    private AddressDto address;
}