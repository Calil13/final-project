package org.example.finalproject.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.example.finalproject.dto.OwnerRequestDto;
import org.example.finalproject.service.OwnersService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/owners")
@Tag(name = "Owners API", description = "Endpoints for Owner role.")
public class OwnersController {

    private final OwnersService vendorsService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/become")
    public String becomeOwner(@RequestBody OwnerRequestDto vendorRequestDTO) {
        return vendorsService.becomeOwner(vendorRequestDTO);
    }
}
