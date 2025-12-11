package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.EmailStartDto;
import org.example.finalproject.dto.EmailVerifyOtpDto;
import org.example.finalproject.dto.UsersUpdateFullNameRequest;
import org.example.finalproject.dto.UserResponseDto;
import org.example.finalproject.exception.BadRequestException;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.exception.NotValidException;
import org.example.finalproject.mapper.UsersMapper;
import org.example.finalproject.repository.OtpRepository;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;
    private final OtpService otpService;
    private final OtpRepository otpRepository;

    public UserResponseDto getUserInfo(String email) {
        var user = usersRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found!");
                    return new NotFoundException("User not found");
                });

        return usersMapper.toResponseDto(user);
    }

    public UsersUpdateFullNameRequest updateFullNameRequest(UsersUpdateFullNameRequest update, String email) {
        var user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setName(update.getName());
        user.setSurname(update.getSurname());

        usersRepository.save(user);

        return usersMapper.toFullNameDto(user);
    }

    public String newEmailRequest(EmailStartDto request, String email) {
        var users = usersRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Email not found!");
                    return new NotFoundException("Email not found");
                });


        if (users.getEmail().equals(request.getEmail())) {
            throw new BadRequestException("New email cannot be the same as current email");
        }

        if (usersRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already used");
        }

        return otpService.sendOtp(request.getEmail());
    }

    public String newEmailVerifyOtp(EmailVerifyOtpDto verify) {

        otpService.verifyOtp(verify.getEmail(), verify.getOtp());

        var otpCode = otpRepository.findByEmail(verify.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

//        user.setEmail(verify.getEmail());
//
//        usersRepository.save(user);

        return "Email updated successfully!";
    }
}
