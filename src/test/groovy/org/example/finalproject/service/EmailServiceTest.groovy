package org.example.finalproject.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import spock.lang.Specification

class EmailServiceTest extends Specification {
    JavaMailSender mailSender = Mock()

    EmailService emailService

    def setup() {
        emailService = new EmailService(mailSender)
    }

    def "sendOtpEmail - should send email with correct OTP format"() {
        given:
        def to = "user@example.com"
        def otpCode = "123456"

        when:
        emailService.sendOtpEmail(to, otpCode)

        then:
        1 * mailSender.send({ SimpleMailMessage msg ->
            msg.to[0] == to
            msg.subject == "Your OTP Code"
            msg.text == "Your OTP code is: 123456"
        })
    }

    def "sendEmail - should send custom email with provided subject and body"() {
        given:
        def to = "recipient@test.com"
        def subject = "Welcome!"
        def body = "Hello, welcome to our platform."

        when:
        emailService.sendEmail(to, subject, body)

        then:
        1 * mailSender.send({ SimpleMailMessage msg ->
            msg.to[0] == to
            msg.subject == subject
            msg.text == body
        })
    }

    def "sendEmail - should throw exception when mailSender fails"() {
        given:
        mailSender.send(_ as SimpleMailMessage) >> { throw new RuntimeException("Mail server down") }

        when:
        emailService.sendEmail("test@test.com", "Sub", "Body")

        then:
        thrown(RuntimeException)
    }
}
