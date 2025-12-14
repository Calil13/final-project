package org.example.finalproject.service

import org.example.finalproject.dto.*
import org.example.finalproject.entity.Users
import org.example.finalproject.exception.BadRequestException
import org.example.finalproject.exception.NotFoundException
import org.example.finalproject.exception.WrongPasswordException
import org.example.finalproject.mapper.UsersMapper
import org.example.finalproject.repository.OtpRepository
import org.example.finalproject.repository.UsersRepository
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext


class UsersServiceTest extends Specification {

    UsersRepository usersRepository = Mock()
    UsersMapper usersMapper = Mock()
    OtpService otpService = Mock()
    OtpRepository otpRepository = Mock()
    PasswordEncoder passwordEncoder = Mock()

    UsersService service = new UsersService(
            usersRepository,
            usersMapper,
            otpService,
            otpRepository,
            passwordEncoder
    )

    def setup() {
        // SecurityContext mock
        def context = Mock(SecurityContext)
        def auth = Mock(Authentication)
        auth.getName() >> "user@mail.com"
        context.getAuthentication() >> auth
        SecurityContextHolder.setContext(context)
    }

    def "getUserInfo returns dto when user exists"() {
        given:
        def user = new Users(email: "a@mail.com")
        def dto = new UserResponseDto()

        usersRepository.findByEmail("a@mail.com") >> Optional.of(user)
        usersMapper.toResponseDto(user) >> dto

        when:
        def result = service.getUserInfo("a@mail.com")

        then:
        result == dto
    }

    def "getUserInfo throws NotFoundException when user missing"() {
        given:
        usersRepository.findByEmail("not@mail.com") >> Optional.empty()

        when:
        service.getUserInfo("not@mail.com")

        then:
        thrown(NotFoundException)
    }

    def "updateFullNameRequest updates name and surname"() {
        given:
        def user = new Users(name: "Old", surname: "OldS")
        def update = new UsersUpdateFullNameRequestDto(name: "New", surname: "NewS")

        usersRepository.findByEmail("u@mail.com") >> Optional.of(user)
        usersMapper.toFullNameDto(user) >> update

        when:
        def result = service.updateFullNameRequest(update, "u@mail.com")

        then:
        user.name == "New"
        user.surname == "NewS"
        result == update
        1 * usersRepository.save(user)
    }

    def "updatePhone successful"() {
        given:
        def user = new Users(email: "user@mail.com")
        usersRepository.findByEmail("user@mail.com") >> Optional.of(user)

        def dto = new UsersUpdatePhoneDto(phone: "551234567")

        when:
        def result = service.updatePhone(dto)

        then:
        user.phone == "+994551234567"
        result == "Phone updated successfully!"
        1 * usersRepository.save(user)
    }

    def "newEmailRequest sends OTP when valid"() {
        given:
        def user = new Users(email: "old@mail.com")
        def req = new EmailSentOtpDto(email: "new@mail.com")

        usersRepository.findByEmail("old@mail.com") >> Optional.of(user)
        usersRepository.existsByEmail("new@mail.com") >> false

        when:
        def result = service.newEmailRequest(req, "old@mail.com")

        then:
        result == "OTP sent successfully!"
        1 * otpService.sendOtp("new@mail.com")
    }

    def "newEmailRequest throws BadRequest when new email same"() {
        given:
        def user = new Users(email: "same@mail.com")
        def req = new EmailSentOtpDto(email: "same@mail.com")

        usersRepository.findByEmail("same@mail.com") >> Optional.of(user)

        when:
        service.newEmailRequest(req, "same@mail.com")

        then:
        thrown(BadRequestException)
    }

    def "newEmailRequest throws BadRequest when email already exists"() {
        given:
        def user = new Users(email: "old@mail.com")
        def req = new EmailSentOtpDto(email: "exists@mail.com")

        usersRepository.findByEmail("old@mail.com") >> Optional.of(user)
        usersRepository.existsByEmail("exists@mail.com") >> true

        when:
        service.newEmailRequest(req, "old@mail.com")

        then:
        thrown(BadRequestException)
    }

    def "newEmailVerifyOtp successfully updates email"() {
        given:
        def user = new Users(email: "old@mail.com")
        def req = new EmailVerifyOtpDto(email: "new@mail.com", otp: "1234")

        usersRepository.findByEmail("user@mail.com") >> Optional.of(user)

        when:
        def result = service.newEmailVerifyOtp(req)

        then:
        1 * otpService.verifyOtp("new@mail.com", "1234")
        user.email == "new@mail.com"
        result == "Email updated successfully!"
        1 * usersRepository.save(user)
        1 * otpService.removeOtp("new@mail.com")
    }


    def "updatePassword fails when current password wrong"() {
        given:
        def dto = new UsersUpdatePasswordRequestDto(
                currentPassword: "wrong",
                newPassword: "new123",
                confirmNewPassword: "new123"
        )
        def user = new Users(password: "encodedPass")

        usersRepository.findByEmail("user@mail.com") >> Optional.of(user)
        passwordEncoder.matches("wrong", "encodedPass") >> false

        when:
        service.updatePassword(dto)

        then:
        thrown(WrongPasswordException)
    }

    def "updatePassword fails when new passwords do not match"() {
        given:
        def dto = new UsersUpdatePasswordRequestDto(
                currentPassword: "cur",
                newPassword: "new1",
                confirmNewPassword: "new2"
        )
        def user = new Users(password: "encoded")

        usersRepository.findByEmail("user@mail.com") >> Optional.of(user)
        passwordEncoder.matches("cur", "encoded") >> true

        when:
        service.updatePassword(dto)

        then:
        thrown(WrongPasswordException)
    }

    def "updatePassword throws when new password same as old"() {
        given:
        def dto = new UsersUpdatePasswordRequestDto(
                currentPassword: "cur",
                newPassword: "same",
                confirmNewPassword: "same"
        )
        def user = new Users(password: "encoded")

        usersRepository.findByEmail("user@mail.com") >> Optional.of(user)
        passwordEncoder.matches("cur", "encoded") >> true
        passwordEncoder.matches("same", "encoded") >> true

        when:
        service.updatePassword(dto)

        then:
        thrown(WrongPasswordException)
    }

    def "updatePassword success"() {
        given:
        def dto = new UsersUpdatePasswordRequestDto(
                currentPassword: "cur",
                newPassword: "new",
                confirmNewPassword: "new"
        )
        def user = new Users(password: "encoded")

        usersRepository.findByEmail("user@mail.com") >> Optional.of(user)
        passwordEncoder.matches("cur", "encoded") >> true
        passwordEncoder.matches("new", "encoded") >> false
        passwordEncoder.encode("new") >> "encodedNew"

        when:
        def result = service.updatePassword(dto)

        then:
        user.password == "encodedNew"
        result == "Password changed successfully!"
        1 * usersRepository.save(user)
    }

    def "deleteAccount fails when password incorrect"() {
        given:
        def dto = new UserCheckPassword(password: "wrong")
        def user = new Users(password: "encoded")

        usersRepository.findByEmail("user@mail.com") >> Optional.of(user)
        passwordEncoder.matches("wrong", "encoded") >> false

        when:
        service.deleteAccount(dto)

        then:
        thrown(WrongPasswordException)
    }

    def "deleteAccount success"() {
        given:
        def dto = new UserCheckPassword(password: "correct")
        def user = new Users(password: "encoded")

        usersRepository.findByEmail("user@mail.com") >> Optional.of(user)
        passwordEncoder.matches("correct", "encoded") >> true

        when:
        def result = service.deleteAccount(dto)

        then:
        result == "Account deleted!"
        1 * usersRepository.delete(user)
    }
}

