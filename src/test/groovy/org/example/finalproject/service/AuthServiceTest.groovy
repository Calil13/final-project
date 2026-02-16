package org.example.finalproject.service

import spock.lang.Specification
import org.example.finalproject.dto.*
import org.example.finalproject.entity.Address
import org.example.finalproject.entity.RefreshToken
import org.example.finalproject.entity.Users
import org.example.finalproject.enums.UserRole
import org.example.finalproject.exception.*
import org.example.finalproject.jwt.JwtUtil
import org.example.finalproject.mapper.AddressMapper
import org.example.finalproject.mapper.UsersMapper
import org.example.finalproject.repository.AddressRepository
import org.example.finalproject.repository.RefreshTokenRepository
import org.example.finalproject.repository.UsersRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDateTime

class AuthServiceTest extends Specification {
    UsersRepository usersRepository = Mock()
    RefreshTokenRepository refreshTokenRepository = Mock()
    UsersMapper usersMapper = Mock()
    PasswordEncoder passwordEncoder = Mock()
    JwtUtil jwtUtil = Mock()
    OtpService otpService = Mock()
    AddressRepository addressRepository = Mock()
    AddressMapper addressMapper = Mock()

    AuthService authService

    def setup() {
        authService = new AuthService(
                usersRepository,
                refreshTokenRepository,
                usersMapper,
                passwordEncoder,
                jwtUtil,
                otpService,
                addressRepository,
                addressMapper
        )
    }

    def "startRegistration - should send OTP when email is not in use"() {
        given:
        def dto = new EmailSentOtpDto(email: "test@example.com")
        usersRepository.findByEmail(dto.email) >> Optional.empty()

        when:
        def result = authService.startRegistration(dto)

        then:
        1 * otpService.sendOtp(dto.email) >> "OTP sent"
        result == "OTP sent"
    }

    def "startRegistration - should throw AlreadyExistsException when email exists"() {
        given:
        def dto = new EmailSentOtpDto(email: "existing@example.com")
        usersRepository.findByEmail(dto.email) >> Optional.of(new Users())

        when:
        authService.startRegistration(dto)

        then:
        thrown(AlreadyExistsException)
    }

    def "finishRegistration - should register user when OTP is verified"() {
        given:
        def registerDto = new RegisterFinishDto(email: "test@example.com", password: "password123", phone: "501234567")
        def addressDto = new AddressDto()
        registerDto.setAddress(addressDto)

        def userEntity = new Users(email: registerDto.email)
        def addressEntity = new Address()

        otpService.isVerified(registerDto.email) >> true
        usersMapper.toEntity(registerDto) >> userEntity
        passwordEncoder.encode(registerDto.password) >> "hashedPassword"
        addressMapper.toEntity(addressDto) >> addressEntity

        when:
        def result = authService.finishRegistration(registerDto)

        then:
        1 * usersRepository.save(_ as Users)
        1 * addressRepository.save(_ as Address)
        1 * otpService.removeOtp(registerDto.email)
        result == "Customer successfully registered."
        userEntity.userRole == UserRole.CUSTOMER
        userEntity.phone == "+994501234567"
    }

    def "login - should return tokens for valid credentials"() {
        given:
        def email = "user@example.com"
        def password = "password"
        def user = new Users(email: email, password: "hashedPassword", userRole: UserRole.CUSTOMER)

        usersRepository.findByEmail(email) >> Optional.of(user)
        passwordEncoder.matches(password, "hashedPassword") >> true
        jwtUtil.generateAccessToken(email) >> "access-token"
        jwtUtil.generateRefreshToken() >> "refresh-token"

        when:
        def response = authService.login(email, password)

        then:
        1 * refreshTokenRepository.deleteByUser(user)
        1 * refreshTokenRepository.save(_ as RefreshToken)
        response.accessToken == "access-token"
        response.refreshToken == "refresh-token"
        user.isActive
    }

    def "login - should throw ProductInUseException when admin tries to login as user"() {
        given:
        def user = new Users(userRole: UserRole.ADMIN)
        usersRepository.findByEmail("admin@test.com") >> Optional.of(user)

        when:
        authService.login("admin@test.com", "any")

        then:
        thrown(ProductInUseException)
    }

    def "refreshToken - should generate new tokens when valid"() {
        given:
        def oldTokenStr = "old-refresh"
        def request = new RefreshTokenRequestDto(refreshToken: oldTokenStr)
        def user = new Users(email: "user@test.com")
        def storedToken = new RefreshToken(
                token: oldTokenStr,
                user: user,
                expiryDate: LocalDateTime.now().plusDays(1),
                revoked: false
        )

        refreshTokenRepository.findByToken(oldTokenStr) >> Optional.of(storedToken)
        jwtUtil.generateAccessToken(user.email) >> "new-access"
        jwtUtil.generateRefreshToken() >> "new-refresh"

        when:
        def response = authService.refreshToken(request)

        then:
        1 * refreshTokenRepository.delete(storedToken)
        1 * refreshTokenRepository.save(_ as RefreshToken)
        response.accessToken == "new-access"
        response.refreshToken == "new-refresh"
    }

    def "resetPassword - should update password when OTP is verified"() {
        given:
        def dto = new UsersForgetPasswordDto(
                email: "test@test.com",
                newPassword: "NewPass123!",
                confirmNewPassword: "NewPass123!"
        )
        def user = new Users(email: dto.email, password: "oldHashedPassword")

        usersRepository.findByEmail(dto.email) >> Optional.of(user)
        otpService.isVerified(dto.email) >> true
        passwordEncoder.matches(dto.newPassword, "oldHashedPassword") >> false
        passwordEncoder.encode(dto.newPassword) >> "newHashedPassword"

        when:
        def result = authService.resetPassword(dto)

        then:
        1 * usersRepository.save(user)
        user.password == "newHashedPassword"
        result == "Password updated successfully."
    }

    def "logout - should delete token and set inactive"() {
        given:
        def email = "test@test.com"
        def password = "password"
        def user = new Users(email: email, password: "hashedPassword")
        def token = new RefreshToken(token: "token-to-delete", user: user)

        def auth = Mock(Authentication)
        auth.getName() >> email
        def securityContext = Mock(SecurityContext)
        securityContext.getAuthentication() >> auth
        SecurityContextHolder.setContext(securityContext)

        usersRepository.findByEmail(email) >> Optional.of(user)
        passwordEncoder.matches(password, "hashedPassword") >> true
        refreshTokenRepository.findByUser(user) >> Optional.of(token)

        when:
        def result = authService.logout(new LogoutRequestDto(password: password))

        then:
        1 * refreshTokenRepository.deleteByToken("token-to-delete")
        !user.isActive
        result == "Logged out successfully!"
    }
}