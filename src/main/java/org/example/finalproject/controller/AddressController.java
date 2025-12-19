package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.finalproject.dto.AddressDto;
import org.example.finalproject.service.AddressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/address")
@Tag(name = "Address API", description = "Address management endpoints")
public class AddressController {

    private final AddressService addressService;

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public AddressDto getAddresses() {
        return addressService.getAddresses();
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/updateAddress")
    public String updateAddress(AddressDto addressDto) {
        return addressService.updateAddress(addressDto);
    }
}
