package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.finalproject.dto.OrderInfoResponseDto;
import org.example.finalproject.dto.OrdersDto;
import org.example.finalproject.enums.DeliveryType;
import org.example.finalproject.service.OrdersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
@Tag(name = "Orders API")
public class OrdersController {

    private final OrdersService ordersService;

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/delivery-info")
    public OrderInfoResponseDto getDeliveryInfo() {
        return ordersService.getDeliveryInfo();
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("{deliveryType}")
    public String createOrder(@Valid @PathVariable DeliveryType deliveryType, @RequestBody OrdersDto ordersDto) {
        return ordersService.createOrder(deliveryType, ordersDto);
    }

    @Operation(
            summary = "Confirm order received"
    )
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/delivered/{orderId}")
    public void received(@PathVariable Long orderId) {
        ordersService.received(orderId);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("return/{orderId}")
    public ResponseEntity<String> returnRental(@PathVariable Long orderId) {
        return ordersService.returnRental(orderId);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{productId}")
    public String deleteOrder(@PathVariable Long productId) {
        return ordersService.deleteOrder(productId);
    }
}
