package org.example.finalproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.finalproject.entity.Address;
import org.example.finalproject.validation.ValidEmail;
import org.example.finalproject.validation.ValidPhone;

@Data
public class RegisterFinishDto {
    @ValidEmail
    private String email;

    private String name;
    private String surname;

    @ValidPhone
    private String phone;

    private String password;
    private AddressDto address;
}