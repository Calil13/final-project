package org.example.finalproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.finalproject.validation.ValidPassword;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersUpdatePasswordRequestDto {
    private String currentPassword;

    @ValidPassword
    private String newPassword;
    private String confirmNewPassword;
}
