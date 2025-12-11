package org.example.finalproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.finalproject.validation.ValidEmail;

@Data
public class RegisterFinishDto {
    @ValidEmail
    private String email;

    private String name;
    private String surname;

    @Pattern(
            regexp = "^\\+994(50|51|55|70|77|99)[0-9]{7}$",
            message = "The phone number is not in the correct format!"
    )
    private String phone;

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least 8 characters, 1 uppercase letter, " +
                      "1 lowercase letter, 1 number, and 1 special character."
    )
    private String password;
}