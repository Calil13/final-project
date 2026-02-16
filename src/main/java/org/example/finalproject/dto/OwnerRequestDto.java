package org.example.finalproject.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OwnerRequestDto {
    private String cardNumber;
    private String cvv;
    private String expireDate;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Zeroes not allowed!")
    @DecimalMin(value = "100.00", message = "Payment must be exactly 100.00 AZN")
    @DecimalMax(value = "100.00", message = "Payment must be exactly 100.00 AZN")
    private BigDecimal amount;
}