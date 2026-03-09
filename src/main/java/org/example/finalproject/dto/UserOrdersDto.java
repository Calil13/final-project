package org.example.finalproject.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.finalproject.entity.Products;
import org.example.finalproject.entity.Users;
import org.example.finalproject.enums.DeliveryType;
import org.example.finalproject.enums.OrderStatus;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserOrdersDto {

    private Integer day;
    private BigDecimal totalAmount;
    private DeliveryType deliveryType;
    private OrderStatus orderStatus;
}
