package org.example.finalproject.dto;

import lombok.Data;

@Data
public class CustomerFinishRegisterDto {
    private String email;
    private String name;
    private String surname;
    private String password;
}