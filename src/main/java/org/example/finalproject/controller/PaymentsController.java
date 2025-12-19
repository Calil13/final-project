package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.finalproject.dto.PayCardDto;
import org.example.finalproject.dto.PaymentResponseDto;
import org.example.finalproject.enums.DeliveryType;
import org.example.finalproject.enums.PaymentMethod;
import org.example.finalproject.service.PaymentsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@AllArgsConstructor
@Tag(name = "Payments API")
public class PaymentsController {

    private final PaymentsService paymentsService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/pay/{paymentMethod}")
    public PaymentResponseDto pay(
            @PathVariable PaymentMethod paymentMethod,
            @RequestBody(required = false) PayCardDto cardDto) {
        return paymentsService.pay(paymentMethod, cardDto);
    }
}
