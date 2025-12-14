package org.example.finalproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.finalproject.validation.ValidEmail;
import org.example.finalproject.validation.ValidPassword;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @ValidEmail
    private String email;

    @ValidPassword
    private String password;
}
