package org.example.finalproject.service;

import lombok.RequiredArgsConstructor;
import org.example.finalproject.dto.RegisterFinishDto;
import org.example.finalproject.dto.RegisterStartDto;
import org.example.finalproject.dto.RegisterVerifyOtpDto;
import org.example.finalproject.entity.Customer;
import org.example.finalproject.entity.Users;
import org.example.finalproject.enums.UserRole;
import org.example.finalproject.exception.AlreadyExistsException;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.exception.OtpNotValidException;
import org.example.finalproject.exception.WrongPasswordException;
import org.example.finalproject.jwt.JwtUtil;
import org.example.finalproject.mapper.UsersMapper;
import org.example.finalproject.repository.CustomerRepository;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final CustomerRepository customerRepository;
    private final UsersMapper customerMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;

    public String startRegistration(RegisterStartDto start) {

        if (usersRepository.findByEmail(start.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Email is already in use!");
        }

        return otpService.sendOtp(start.getEmail());
    }

    public String verifyOtp(RegisterVerifyOtpDto verify) {
        otpService.verifyOtp(verify.getEmail(), verify.getOtp());
        return "OTP verified!";
    }

    public String finishRegistration(RegisterFinishDto finish) {

        if (!otpService.isVerified(finish.getEmail())) {
            throw new OtpNotValidException("OTP not verified!");
        }

        Users user = customerMapper.toEntity(finish);
        user.setPassword(passwordEncoder.encode(finish.getPassword()));
        user.setUserRole(UserRole.ROLE_CUSTOMER);
        usersRepository.save(user);

        Customer customer = Customer.builder()
                .user(user)
                .build();
        customerRepository.save(customer);

        otpService.removeOtp(finish.getEmail());

        return "Customer successfully registered!";
    }

    public String login(String email, String password) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new WrongPasswordException("Password is Wrong!");
        }

        return jwtUtil.generateToken(email);
    }
}

