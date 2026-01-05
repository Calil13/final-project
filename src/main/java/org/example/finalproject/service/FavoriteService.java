package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.FavoriteProductsDto;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class FavoriteService {

    private final UsersRepository usersRepository;

    public Page<FavoriteProductsDto> getFavorites(Pageable pageable){
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var customer = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("Customer not found!"));


    }
}
