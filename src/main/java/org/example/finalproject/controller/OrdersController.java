package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.finalproject.dto.DeliveryInfoResponseDto;
import org.example.finalproject.dto.OrdersDto;
import org.example.finalproject.service.OrdersService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Orders API")
public class OrdersController {

    private final OrdersService ordersService;

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/delivery-info")
    public DeliveryInfoResponseDto getDeliveryInfo() {
        return ordersService.getDeliveryInfo();
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public String createOrder(@Valid @RequestBody OrdersDto ordersDto) {
        return ordersService.createOrder(ordersDto);
    }

    @Operation(
            summary = "Confirm order received"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/delivered")
    public void received() {
        ordersService.received();
    }
}
