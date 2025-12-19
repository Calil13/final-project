package org.example.finalproject.repository;

import org.example.finalproject.entity.Payment;
import org.example.finalproject.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByUser(Users user);
}
