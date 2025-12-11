package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.UpdateFullNameRequest;
import org.example.finalproject.dto.UserResponseDto;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.mapper.UsersMapper;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UsersService {

    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;

    public UserResponseDto getUserInfo(String email) {
        var user = usersRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found!");
                    return new NotFoundException("User not found");
                });

        return usersMapper.toResponseDto(user);
    }

    public UpdateFullNameRequest updateFullNameRequest(UpdateFullNameRequest update, String email) {
        var user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setName(update.getName());
        user.setSurname(update.getSurname());

        usersRepository.save(user);

        return usersMapper.toFullNameDto(user);
    }
}
