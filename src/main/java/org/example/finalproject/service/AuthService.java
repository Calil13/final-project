package org.example.finalproject.service;

import lombok.RequiredArgsConstructor;
import org.example.finalproject.dto.CustomerFinishRegisterDto;
import org.example.finalproject.dto.CustomerStartDto;
import org.example.finalproject.dto.CustomerVerifyOtpDto;
import org.example.finalproject.entity.Users;
import org.example.finalproject.exception.AlreadyExistsException;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.exception.OtpNotValidException;
import org.example.finalproject.jwt.JwtUtil;
import org.example.finalproject.mapper.CustomerMapper;
import org.example.finalproject.repository.OtpRepository;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpRepository otpRepository;
    private final OtpService otpService;

    public String startRegistration(CustomerStartDto start) {

        if (usersRepository.findByEmail(start.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Email is already in use!");
        }

        return otpService.sendOtp(start.getEmail());
    }

    public String verifyOtp(CustomerVerifyOtpDto verify) {
        otpService.verifyOtp(verify.getOtp());
        return "OTP verified!";
    }

    public String finishRegistration(CustomerFinishRegisterDto finish) {

        if (!otpService.isVerified(finish.getEmail())) {
            throw new OtpNotValidException("OTP not verified!");
        }

        Users user = customerMapper.toEntity(finish);

        user.setPassword(passwordEncoder.encode(finish.getPassword()));

        usersRepository.save(user);

        otpService.removeOtp(finish.getEmail());

        return "Customer successfully registered!";
    }

    public String login(String email, String password) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        return jwtUtil.generateToken(email);
    }
}

