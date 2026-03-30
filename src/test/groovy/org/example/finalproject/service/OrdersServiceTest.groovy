package org.example.finalproject.service

import org.example.finalproject.dto.*
import org.example.finalproject.entity.*
import org.example.finalproject.enums.*
import org.example.finalproject.exception.*
import org.example.finalproject.mapper.*
import org.example.finalproject.repository.*
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification

import java.math.BigDecimal
import java.time.LocalDateTime

class OrdersServiceTest extends Specification {

    def ordersRepository = Mock(OrdersRepository)
    def ordersMapper = Mock(OrdersMapper)
    def usersRepository = Mock(UsersRepository)
    def productsRepository = Mock(ProductsRepository)
    def addressMapper = Mock(AddressMapper)
    def addressRepository = Mock(AddressRepository)
    def paymentRepository = Mock(PaymentRepository)

    def service = new OrdersService(
            ordersRepository,
            ordersMapper,
            usersRepository,
            productsRepository,
            addressMapper,
            addressRepository,
            paymentRepository
    )

    def setupSecurity(String email) {
        Authentication auth = Mock()
        auth.getName() >> email
        SecurityContext context = Mock()
        context.getAuthentication() >> auth
        SecurityContextHolder.setContext(context)
    }

    def "should return user's orders"() {
        given:
        setupSecurity("user@mail.com")
        def user = new Users(id:1)
        def order = new Orders()
        def dto = new UserOrdersDto()
        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        ordersRepository.findByCustomer(user, PageRequest.of(0,10)) >> new PageImpl([order])
        ordersMapper.toDto(order) >> dto

        when:
        def result = service.getOrders(PageRequest.of(0,10))

        then:
        result.content.size() == 1
    }

    def "should throw exception if user not found in getOrders"() {
        given:
        setupSecurity("user@mail.com")
        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.empty()

        when:
        service.getOrders(PageRequest.of(0,10))

        then:
        thrown(NotFoundException)
    }

    def "should return delivery info"() {
        given:
        setupSecurity("user@mail.com")
        def user = new Users(id:1, phone:"0123456789")
        def address = new Address()
        def addressDto = new AddressDto()
        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        addressRepository.findByUser(user) >> Optional.of(address)
        addressMapper.toDto(address) >> addressDto

        when:
        def result = service.getDeliveryInfo()

        then:
        result.phone == "0123456789"
        result.address == addressDto
    }

    def "should throw exception if address not found"() {
        given:
        setupSecurity("user@mail.com")
        def user = new Users()
        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        addressRepository.findByUser(user) >> Optional.empty()

        when:
        service.getDeliveryInfo()

        then:
        thrown(NotFoundException)
    }

    def "should create order successfully"() {
        given:
        setupSecurity("user@mail.com")
        def user = new Users(id:1)
        def product = new Products(id:1, price:BigDecimal.valueOf(10), isAvailable:true)
        def ordersDto = new OrdersDto(productId:1, day:3)

        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        productsRepository.findById(1) >> Optional.of(product)

        when:
        def result = service.createOrder(DeliveryType.DELIVERY, ordersDto)

        then:
        product.isAvailable == false
        result.contains("30")
    }

    def "should throw exception if product not available"() {
        given:
        setupSecurity("user@mail.com")
        def user = new Users(id:1)
        def product = new Products(id:1, isAvailable:false)
        def ordersDto = new OrdersDto(productId:1, day:1)

        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        productsRepository.findById(1) >> Optional.of(product)

        when:
        service.createOrder(DeliveryType.DELIVERY, ordersDto)

        then:
        thrown(ProductNotAvailableException)
    }

    def "should mark order as delivered"() {
        given:
        setupSecurity("user@mail.com")
        def user = new Users(id:1)
        def order = new Orders(orderStatus:OrderStatus.CREATED)
        def payment = new Payment(paymentMethod:PaymentMethod.CASH, paymentStatus:PaymentStatus.PENDING)
        paymentRepository.findByOrder(order) >> Optional.of(payment)
        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        ordersRepository.findById(1) >> Optional.of(order)

        when:
        service.received(1)

        then:
        order.orderStatus == OrderStatus.DELIVERED
        payment.paymentStatus == PaymentStatus.SUCCESS
    }

    def "should throw exception if order already delivered"() {
        given:
        setupSecurity("user@mail.com")
        def user = new Users()
        def order = new Orders(orderStatus:OrderStatus.DELIVERED)
        def payment = new Payment(paymentMethod: PaymentMethod.CASH, paymentStatus: PaymentStatus.PENDING)
        paymentRepository.findByOrder(order) >> Optional.of(payment)
        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        ordersRepository.findById(1) >> Optional.of(order)

        when:
        service.received(1)

        then:
        thrown(AlreadyExistsException)
    }

    def "should return product successfully"() {
        given:
        setupSecurity("user@mail.com")
        def user = new Users()
        def product = new Products(id:1, isAvailable:false)
        def order = new Orders(orderDate:LocalDateTime.now().minusDays(1), product:product)

        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        ordersRepository.findById(1) >> Optional.of(order)

        when:
        def result = service.returnRental(1)

        then:
        product.isAvailable
        order.orderStatus == OrderStatus.RETURNED
        result == "The product has been returned."
    }

    def "should fail return if product not expired"() {
        given:
        setupSecurity("user@mail.com")
        def user = new Users()
        def product = new Products(id:1, isAvailable:false)
        def order = new Orders(orderDate:LocalDateTime.now().plusDays(1), product:product)

        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        ordersRepository.findById(1) >> Optional.of(order)

        when:
        def result = service.returnRental(1)

        then:
        result == "The product has not expired yet."
    }

    def "should delete order successfully"() {
        given:
        setupSecurity("user@mail.com")
        def user = new Users()
        def product = new Products(id:1, isAvailable:false)
        def order = new Orders(product:product)

        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        productsRepository.findById(1) >> Optional.of(product)
        ordersRepository.findByProduct(product) >> Optional.of(order)

        when:
        def result = service.deleteOrder(1)

        then:
        product.isAvailable
        result.contains("cancelled")
    }

    def "should throw exception if order not found"() {
        given:
        setupSecurity("user@mail.com")
        def user = new Users()
        def product = new Products(id:1, isAvailable:false)

        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        productsRepository.findById(1) >> Optional.of(product)
        ordersRepository.findByProduct(product) >> Optional.empty()

        when:
        service.deleteOrder(1)

        then:
        thrown(NotFoundException)
    }
}