package org.example.finalproject.service

import org.example.finalproject.dto.*
import org.example.finalproject.entity.Address
import org.example.finalproject.entity.Payment
import org.example.finalproject.entity.Users
import org.example.finalproject.enums.*
import org.example.finalproject.exception.*
import org.example.finalproject.mapper.AddressMapper
import org.example.finalproject.mapper.UsersMapper
import org.example.finalproject.repository.AddressRepository
import org.example.finalproject.repository.PaymentRepository
import org.example.finalproject.repository.UsersRepository
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class UsersServiceTest extends Specification {

    def usersRepository = Mock(UsersRepository)
    def usersMapper = Mock(UsersMapper)
    def otpService = Mock(OtpService)
    def passwordEncoder = Mock(PasswordEncoder)
    def paymentRepository = Mock(PaymentRepository)
    def paymentsService = Mock(PaymentsService)
    def addressRepository = Mock(AddressRepository)
    def addressMapper = Mock(AddressMapper)

    def service = new UsersService(
            usersRepository,
            usersMapper,
            otpService,
            passwordEncoder,
            paymentRepository,
            paymentsService,
            addressRepository,
            addressMapper
    )

    def setup() {
        SecurityContextHolder.context.authentication =
                new UsernamePasswordAuthenticationToken("user@mail.com", null)
    }

    def cleanup() {
        SecurityContextHolder.clearContext()
    }

    def "getUserInfo should return dto"() {
        given:
        def user = new Users(id: 1L, email: "user@mail.com")
        def address = Mock(Address)
        def addressDto = Mock(AddressDto)
        def responseDto = Mock(UserResponseDto)

        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        addressRepository.findByUser(user) >> Optional.of(address)
        addressMapper.toDto(address) >> addressDto
        usersMapper.toResponseDto(user, addressDto as AddressDto) >> responseDto

        when:
        def result = service.getUserInfo()

        then:
        result == responseDto
    }

    def "updatePassword should change password when valid"() {
        given:
        def user = new Users(id: 1L, email: "user@mail.com", password: "encodedOld")

        def dto = new UsersUpdatePasswordRequestDto(
                currentPassword: "old",
                newPassword: "newPass",
                confirmNewPassword: "newPass"
        )

        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        passwordEncoder.matches("old", "encodedOld") >> true
        passwordEncoder.matches("newPass", "encodedOld") >> false
        passwordEncoder.encode("newPass") >> "encodedNew"

        when:
        def result = service.updatePassword(dto)

        then:
        user.password == "encodedNew"
        1 * usersRepository.save(user)
        result == "Password changed successfully!"
    }

    def "updatePassword should throw when current password wrong"() {
        given:
        def user = new Users(password: "encodedOld")
        def dto = new UsersUpdatePasswordRequestDto(currentPassword: "wrong")

        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        passwordEncoder.matches("wrong", "encodedOld") >> false

        when:
        service.updatePassword(dto)

        then:
        thrown(WrongPasswordException)
    }

    def "deleteAccount should mark user deleted"() {
        given:
        def user = new Users(password: "encoded", deleted: false)

        def dto = new UserCheckPassword(password: "pass")

        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        passwordEncoder.matches("pass", "encoded") >> true

        when:
        def result = service.deleteAccount(dto)

        then:
        user.deleted == true
        user.isActive == false
        1 * usersRepository.save(user)
        result == "Account deleted."
    }

    def "becomeOwner should upgrade role when payment valid"() {
        given:
        def user = new Users(id: 1L, email: "user@mail.com", userRole: UserRole.CUSTOMER)

        def dto = new OwnerRequestDto(
                cardNumber: "1234567812345678",
                cvv: "123",
                expireDate: "12/30",
                amount: 100
        )

        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        paymentsService.validateCard(_ as String, _ as String, _ as String) >> null

        when:
        def result = service.becomeOwner(dto)

        then:
        2 * paymentRepository.save(_ as Payment)
        1 * usersRepository.save(user)
        user.userRole == UserRole.OWNER
        result == "Customer successfully became a OWNER."
    }

    def "becomeOwner should return failure when validation fails"() {
        given:
        def user = new Users(userRole: UserRole.CUSTOMER)

        def dto = new OwnerRequestDto(
                cardNumber: "1111",
                cvv: "123",
                expireDate: "12/30"
        )

        usersRepository.findByEmailAndDeletedFalse("user@mail.com") >> Optional.of(user)
        paymentsService.validateCard(_ as String, _ as String, _ as String) >> "Invalid card"

        when:
        def result = service.becomeOwner(dto)

        then:
        result.contains("FAILED")
        0 * paymentRepository.save(_)
    }
}
