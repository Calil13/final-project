package org.example.finalproject.service

import org.example.finalproject.dto.OrdersDto
import org.example.finalproject.entity.Orders
import org.example.finalproject.entity.Payment
import org.example.finalproject.entity.Products
import org.example.finalproject.entity.Users
import org.example.finalproject.enums.*
import org.example.finalproject.exception.ProductNotAvailableException
import org.example.finalproject.exception.AlreadyExistsException
import org.example.finalproject.repository.*
import org.example.finalproject.mapper.AddressMapper
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification


class OrdersServiceTest extends Specification {
    OrdersRepository ordersRepository = Mock()
    UsersRepository usersRepository = Mock()
    ProductsRepository productsRepository = Mock()
    AddressMapper addressMapper = Mock()
    AddressRepository addressRepository = Mock()
    PaymentRepository paymentRepository = Mock()

    OrdersService ordersService

    def setup() {
        ordersService = new OrdersService(
                ordersRepository,
                usersRepository,
                productsRepository,
                addressMapper,
                addressRepository,
                paymentRepository
        )

        def auth = new UsernamePasswordAuthenticationToken("test@mail.com", null)
        SecurityContextHolder.context.authentication = auth
    }

    def cleanup() {
        SecurityContextHolder.clearContext()
    }

    def "createOrder should create order successfully"() {
        given:
        def user = new Users(email: "test@mail.com")
        def product = new Products(
                id: 1L,
                price: new BigDecimal("10"),
                isAvailable: true
        )

        def dto = new OrdersDto(productId: 1L, day: 3)

        usersRepository.findByEmailAndDeletedFalse(_ as String) >> Optional.of(user)
        productsRepository.findById(1L) >> Optional.of(product)

        when:
        def result = ordersService.createOrder(DeliveryType.DELIVERY, dto)

        then:
        1 * ordersRepository.save(_ as Orders)
        1 * productsRepository.save(product)

        product.isAvailable == false
        result == "Your total amount : 30 AZN"
    }

    def "createOrder should throw exception if product is not available"() {
        given:
        def user = new Users(email: "test@mail.com")
        def product = new Products(
                id: 1L,
                isAvailable: false
        )

        def dto = new OrdersDto(productId: 1L, day: 2)

        usersRepository.findByEmailAndDeletedFalse(_ as String) >> Optional.of(user)
        productsRepository.findById(1L) >> Optional.of(product)

        when:
        ordersService.createOrder(DeliveryType.DELIVERY, dto)

        then:
        thrown(ProductNotAvailableException)
        0 * ordersRepository.save(_)
    }

    def "received should mark order as delivered and payment success for cash"() {
        given:
        def user = new Users(email: "test@mail.com")
        def order = new Orders(orderStatus: OrderStatus.CREATED)
        def payment = Mock(Payment) {
            getPaymentMethod() >> PaymentMethod.CASH
        }

        usersRepository.findByEmailAndDeletedFalse(_ as String) >> Optional.of(user)
        ordersRepository.findById(1L) >> Optional.of(order)
        paymentRepository.findByOrder(order) >> Optional.of(payment)

        when:
        ordersService.received(1L)

        then:
        1 * payment.setPaymentStatus(PaymentStatus.SUCCESS)
        order.orderStatus == OrderStatus.DELIVERED
        1 * ordersRepository.save(order)
    }

    def "received should throw exception if order already delivered"() {
        given:
        def user = new Users(email: "test@mail.com")
        def order = new Orders(orderStatus: OrderStatus.DELIVERED)
        def payment = new Payment(
                paymentMethod: PaymentMethod.CASH,
                paymentStatus: PaymentStatus.PENDING
        )

        usersRepository.findByEmailAndDeletedFalse(_ as String) >> Optional.of(user)
        ordersRepository.findById(1L) >> Optional.of(order)
        paymentRepository.findByOrder(order) >> Optional.of(payment)

        when:
        ordersService.received(1L)

        then:
        thrown(AlreadyExistsException)
    }

    def "deleteOrder should cancel order and free product"() {
        given:
        def user = new Users(email: "test@mail.com")
        def product = new Products(isAvailable: false)
        def order = new Orders(product: product)

        usersRepository.findByEmailAndDeletedFalse(_) >> Optional.of(user)
        productsRepository.findById(1L) >> Optional.of(product)
        ordersRepository.findByProduct(product) >> Optional.of(order)

        when:
        def result = ordersService.deleteOrder(1L)

        then:
        product.isAvailable == true
        1 * productsRepository.save(product)
        1 * ordersRepository.delete(order)
        result == "The order has been cancelled."
    }
}
