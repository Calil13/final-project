package org.example.finalproject.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.entity.Orders;
import org.example.finalproject.entity.Users;
import org.example.finalproject.enums.OrderStatus;
import org.example.finalproject.repository.OrdersRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OverdueEmailService {

    private final OrdersRepository ordersRepository;
    private final EmailService emailService;

    @Transactional
    public void notifyOverdueOrders() {

        LocalDateTime now = LocalDateTime.now();

        List<Orders> orders =
                ordersRepository.findByOrderStatusNot(OrderStatus.RETURNED);

        for (Orders order : orders) {

            if (order.getOrderStatus() == OrderStatus.CANCELLED) {
                continue;
            }

            LocalDateTime endDate =
                    order.getOrderDate().plusDays(order.getDay());

            if (now.isAfter(endDate)) {

                Users user = order.getCustomer();

                String subject = "Kirayə müddəti başa çatmışdır!";

                String body =
                        "Hörmətli " + user.getName() + ",\n\n" +
                        "Sifariş №" + order.getId() + " üçün kirayə müddəti başa çatmışdır.\n" +
                        "Zəhmət olmasa məhsulu ən qısa zamanda geri qaytarın.\n\n" +
                        "Əks halda balansınızdan əlavə ödəniş tutulacaqdır.\n\n" +
                        "Hörmətlə,\nFinal Project Team";

                emailService.sendEmail(user.getEmail(), subject, body);

                log.info("Overdue email sent to user {} for order {}",
                        user.getEmail(), order.getId());
            }
        }
    }
}
