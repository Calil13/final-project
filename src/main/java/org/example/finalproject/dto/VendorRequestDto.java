package org.example.finalproject.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VendorRequestDto {

    private Long customerId;
    private String storeName;

    private String cardNumber;
    private String cvv;
    private String expireDate;

    private BigDecimal amount;
}
