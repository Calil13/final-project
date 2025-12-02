package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.example.finalproject.dto.VendorRequestDTO;
import org.example.finalproject.service.VendorsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/vendors")
public class VendorsController {

    private final VendorsService vendorsService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/become")
    public String becomeVendor(@RequestBody VendorRequestDTO vendorRequestDTO) {
        return vendorsService.becomeVendor(vendorRequestDTO);
    }
}
