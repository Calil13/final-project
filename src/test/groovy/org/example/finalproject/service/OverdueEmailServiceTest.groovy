package org.example.finalproject.service

import org.example.finalproject.entity.Orders
import org.example.finalproject.entity.Users
import org.example.finalproject.enums.OrderStatus
import org.example.finalproject.repository.OrdersRepository
import java.time.LocalDateTime;
import spock.lang.Specification

class OverdueEmailServiceTest extends Specification {
    OrdersRepository ordersRepository = Mock()
    EmailService emailService = Mock()

    OverdueEmailService overdueEmailService =
            new OverdueEmailService(ordersRepository, emailService)

    def "should send email for overdue orders"() {
        given: "Overdue order and user"
        Users user = new Users()
        user.setEmail("test@mail.com")
        user.setName("Test User")

        Orders order = new Orders()
        order.setId(1L)
        order.setCustomer(user)
        order.setOrderStatus(OrderStatus.CREATED)
        order.setOrderDate(LocalDateTime.now().minusDays(5))
        order.setDay(3)

        ordersRepository.findByOrderStatusNot(OrderStatus.RETURNED) >> [order]

        when: "Overdue check is executed"
        overdueEmailService.notifyOverdueOrders()

        then: "Email should be sent"
        1 * emailService.sendEmail(
                "test@mail.com",
                _ as String,
                _ as String
        )
    }
}
