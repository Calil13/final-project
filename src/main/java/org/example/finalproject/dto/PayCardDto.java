package org.example.finalproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayCardDto {
    private Long orderId;

    @NotBlank(message = "Card number is required")
    private String cardNumber;

    @NotBlank(message = "CVV is required")
    private String cvv;

    @NotBlank(message = "Expire date is required")
    private String expireDate;

    private BigDecimal amount;
}
