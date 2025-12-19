package org.example.finalproject.repository;

import org.example.finalproject.entity.Orders;
import org.example.finalproject.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByCustomer(Users customer);
}
