package org.example.finalproject.service

import org.example.finalproject.entity.OtpCode
import org.example.finalproject.exception.BadRequestException
import org.example.finalproject.exception.NotFoundException
import org.example.finalproject.exception.NotValidException
import org.example.finalproject.repository.OtpRepository
import org.springframework.mail.MailException
import spock.lang.Specification
import java.time.LocalDateTime

class OtpServiceTest extends Specification {
    OtpRepository otpRepository = Mock()
    EmailService emailService = Mock()

    OtpService otpService

    def setup() {
        otpService = new OtpService(otpRepository, emailService)
    }

    def "sendOtp - should delete old OTP, save new one and send email"() {
        given:
        def email = "test@example.com"

        when:
        def result = otpService.sendOtp(email)

        then:
        1 * otpRepository.deleteByEmail(email)
        1 * otpRepository.save({ OtpCode otp ->
            otp.email == email &&
                    otp.otp.length() == 6 &&
                    otp.expiresAt.isAfter(LocalDateTime.now())
        })
        1 * emailService.sendOtpEmail(email, _ as String)
        result == "OTP sent successfully!"
    }

    def "sendOtp - should throw BadRequestException when email sending fails"() {
        given:
        def email = "wrong@email.com"
        emailService.sendOtpEmail(email, _ as String) >> { throw new MailException("Failed") {} }

        when:
        otpService.sendOtp(email)

        then:
        thrown(BadRequestException)
    }

    def "verifyOtp - should set verified to true when OTP is correct and not expired"() {
        given:
        def email = "test@example.com"
        def code = "123456"
        def otpEntity = new OtpCode(
                email: email,
                otp: code,
                expiresAt: LocalDateTime.now().plusMinutes(5),
                verified: false
        )
        otpRepository.findByEmail(email) >> Optional.of(otpEntity)

        when:
        otpService.verifyOtp(email, code)

        then:
        otpEntity.verified
        1 * otpRepository.save(otpEntity)
    }

    def "verifyOtp - should throw RuntimeException when OTP is expired"() {
        given:
        def email = "test@example.com"
        def otpEntity = new OtpCode(
                email: email,
                otp: "123456",
                expiresAt: LocalDateTime.now().minusMinutes(1)
        )
        otpRepository.findByEmail(email) >> Optional.of(otpEntity)

        when:
        otpService.verifyOtp(email, "123456")

        then:
        def ex = thrown(RuntimeException)
        ex.message == "OTP has expired!"
    }

    def "verifyOtp - should throw NotValidException when OTP is wrong"() {
        given:
        def otpEntity = new OtpCode(otp: "123456", expiresAt: LocalDateTime.now().plusMinutes(5))
        otpRepository.findByEmail(_ as String) >> Optional.of(otpEntity)

        when:
        otpService.verifyOtp("test@test.com", "wrong_code")

        then:
        thrown(NotValidException)
    }

    def "isVerified - should return correct status"() {
        given:
        otpRepository.findByEmail("verified@test.com") >> Optional.of(new OtpCode(verified: true))
        otpRepository.findByEmail("notverified@test.com") >> Optional.of(new OtpCode(verified: false))
        otpRepository.findByEmail("none@test.com") >> Optional.empty()

        expect:
        otpService.isVerified("verified@test.com")
        !otpService.isVerified("notverified@test.com")
        !otpService.isVerified("none@test.com")
    }

    def "removeOtp - should call repository delete"() {
        when:
        otpService.removeOtp("test@test.com")

        then:
        1 * otpRepository.deleteByEmail("test@test.com")
    }
}
