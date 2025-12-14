package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.finalproject.dto.VendorChangeStoreNameDto;
import org.example.finalproject.dto.VendorRequestDto;
import org.example.finalproject.service.VendorsService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/vendors")
@Tag(name = "Vendors API", description = "Endpoints for Vendor role.")
public class VendorsController {

    private final VendorsService vendorsService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/become")
    public String becomeVendor(@RequestBody VendorRequestDto vendorRequestDTO) {
        return vendorsService.becomeVendor(vendorRequestDTO);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/change/store-name")
    public String changeStoreName(VendorChangeStoreNameDto changeStoreName) {
        return vendorsService.changeStoreName(changeStoreName);
    }
}
