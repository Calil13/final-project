package org.example.finalproject.repository;

import org.example.finalproject.entity.Orders;
import org.example.finalproject.entity.Products;
import org.example.finalproject.entity.Users;
import org.example.finalproject.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByCustomer(Users customer);
    Optional<Orders> findByCustomerAndOrderStatus(Users customer, OrderStatus orderStatus);
    List<Orders> findByOrderStatusNot(OrderStatus orderStatus);
    Optional<Orders> findByProduct(Products product);

}
