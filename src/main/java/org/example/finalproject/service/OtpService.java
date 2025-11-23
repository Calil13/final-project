package org.example.finalproject.service;

import lombok.RequiredArgsConstructor;
import org.example.finalproject.entity.OtpCode;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.exception.OtpNotValidException;
import org.example.finalproject.repository.OtpRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;

    public String sendOtp(String email) {

        otpRepository.deleteByEmail(email);

        String code = String.valueOf((int)(Math.random() * 900000) + 100000);

        OtpCode otp = OtpCode.builder()
                .email(email)
                .otp(code)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        otpRepository.save(otp);

        System.out.println("OTP sent to email: " + email + " Code: " + code);

        return "OTP sent successfully!";
    }

    public void verifyOtp(String Otp) {

        OtpCode otp = otpRepository.findByEmail(Otp)
                .orElseThrow(() -> new NotFoundException("OTP not found!"));

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired!");
        }

        if (!otp.getOtp().equals(Otp)) {
            throw new OtpNotValidException("OTP is wrong!");
        }

        otp.setVerified(true);
        otpRepository.save(otp);

    }

    public boolean isVerified(String email) {
        return otpRepository.findByEmail(email)
                .map(OtpCode::isVerified)
                .orElse(false);
    }

    public void removeOtp(String email) {
        otpRepository.deleteByEmail(email);
    }
}

