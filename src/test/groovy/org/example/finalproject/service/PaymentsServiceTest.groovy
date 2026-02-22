package org.example.finalproject.service

import org.example.finalproject.dto.PayCardDto
import org.example.finalproject.entity.*
import org.example.finalproject.enums.*
import org.example.finalproject.exception.NotFoundException
import org.example.finalproject.repository.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

class PaymentsServiceTest extends Specification {

    UsersRepository usersRepository = Mock()
    OrdersRepository ordersRepository = Mock()
    PaymentRepository paymentRepository = Mock()
    AddressRepository addressRepository = Mock()

    PaymentsService paymentsService =
            new PaymentsService(
                    usersRepository,
                    ordersRepository,
                    paymentRepository,
                    addressRepository
            )

    def setup() {
        SecurityContextHolder.context.authentication =
                new UsernamePasswordAuthenticationToken("test@mail.com", null)
    }

    def cleanup() {
        SecurityContextHolder.clearContext()
    }

    def "CARD payment - successful payment"() {
        given:
        Users customer = new Users(email: "test@mail.com", name: "Test")
        Orders order = new Orders(
                orderStatus: OrderStatus.CREATED,
                totalAmount: 100,
                deliveryType: DeliveryType.DELIVERY
        )

        PayCardDto cardDto = new PayCardDto(
                cardNumber: "1234567812345678",
                cvv: "123",
                expireDate: "12/26",
                amount: 100
        )

        usersRepository.findByEmailAndDeletedFalse("test@mail.com") >> Optional.of(customer)
        ordersRepository.findByCustomerAndOrderStatus(customer, OrderStatus.CREATED) >> Optional.of(order)

        when:
        def response = paymentsService.pay(PaymentMethod.CARD, cardDto)

        then:
        1 * paymentRepository.save(_ as Payment)
        1 * ordersRepository.save(order)
        response.message.contains("Payment completed successfully")
        order.orderStatus == OrderStatus.PAID
    }

    def "CARD payment - invalid card data"() {
        given:
        Users customer = new Users(email: "test@mail.com")
        Orders order = new Orders(orderStatus: OrderStatus.CREATED, totalAmount: 100)

        PayCardDto cardDto = new PayCardDto(
                cardNumber: "123",
                cvv: "12",
                expireDate: "13/99",
                amount: 100
        )

        usersRepository.findByEmailAndDeletedFalse(_ as String) >> Optional.of(customer)
        ordersRepository.findByCustomerAndOrderStatus(customer, OrderStatus.CREATED) >> Optional.of(order)

        when:
        def response = paymentsService.pay(PaymentMethod.CARD, cardDto)

        then:
        0 * paymentRepository.save(_)
        response.message.contains("Payment FAILED")
    }

    def "CARD payment - amount mismatch should throw exception"() {
        given:
        Users customer = new Users(email: "test@mail.com")
        Orders order = new Orders(orderStatus: OrderStatus.CREATED, totalAmount: 200)

        PayCardDto cardDto = new PayCardDto(
                cardNumber: "1234567812345678",
                cvv: "123",
                expireDate: "12/26",
                amount: 100
        )

        usersRepository.findByEmailAndDeletedFalse(_ as String) >> Optional.of(customer)
        ordersRepository.findByCustomerAndOrderStatus(customer, OrderStatus.CREATED) >> Optional.of(order)

        when:
        paymentsService.pay(PaymentMethod.CARD, cardDto)

        then:
        thrown(org.example.finalproject.exception.IllegalArgumentException)
    }

    def "CASH payment - successful"() {
        given:
        Users customer = new Users(email: "test@mail.com")
        Orders order = new Orders(
                orderStatus: OrderStatus.CREATED,
                totalAmount: 150,
                deliveryType: DeliveryType.DELIVERY
        )

        usersRepository.findByEmailAndDeletedFalse(_ as String) >> Optional.of(customer)
        ordersRepository.findByCustomerAndOrderStatus(customer, OrderStatus.CREATED) >> Optional.of(order)

        when:
        def response = paymentsService.pay(PaymentMethod.CASH, null)

        then:
        1 * paymentRepository.save(_ as Payment)
        order.orderStatus == OrderStatus.PENDING
        response.message.contains("Cash payment")
    }

    def "payment should fail when active order not found"() {
        given:
        Users customer = new Users(email: "test@mail.com")

        usersRepository.findByEmailAndDeletedFalse(_ as String) >> Optional.of(customer)
        ordersRepository.findByCustomerAndOrderStatus(customer, OrderStatus.CREATED) >> Optional.empty()

        when:
        paymentsService.pay(PaymentMethod.CASH, null)

        then:
        thrown(NotFoundException)
    }
}
