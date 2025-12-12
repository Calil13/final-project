package org.example.finalproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.finalproject.validation.ValidEmail;
import org.example.finalproject.validation.ValidPassword;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersForgetPasswordDto {

    @ValidEmail
    private String email;

    @ValidPassword
    private String newPassword;
    private String confirmNewPassword;
}
