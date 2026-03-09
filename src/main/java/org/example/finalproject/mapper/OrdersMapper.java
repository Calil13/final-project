package org.example.finalproject.mapper;

import org.example.finalproject.dto.UserOrdersDto;
import org.example.finalproject.entity.Orders;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrdersMapper {
    UserOrdersDto toDto(Orders orders);
}
