package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.AddressDto;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.mapper.AddressMapper;
import org.example.finalproject.repository.AddressRepository;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AddressService {

    private final UsersRepository usersRepository;
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    public AddressDto getAddresses() {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmailAndDeletedFalse(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        var address = addressRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.error("Address not found!");
                    return new NotFoundException("Address not found!");
                });

        return addressMapper.toResponseDto(address);
    }

    public String updateAddress(AddressDto addressDto) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmailAndDeletedFalse(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        var address = addressRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Address not found!"));

        address.setCity(addressDto.getCity());
        address.setStreet(addressDto.getStreet());
        address.setHome(addressDto.getHome());

        addressRepository.save(address);

        log.info("Address updated for user with ID: {}", user.getId());
        return "Address changed successfully.";
    }
}
