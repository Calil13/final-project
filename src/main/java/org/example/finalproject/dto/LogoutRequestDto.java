package org.example.finalproject.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequestDto {
    @NotBlank
    private String password;
}
