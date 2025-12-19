package org.example.finalproject.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayCardDto {
    private Long orderId;
    private String cardNumber;
    private String cvv;
    private String expireDate;
    private BigDecimal amount;
}
