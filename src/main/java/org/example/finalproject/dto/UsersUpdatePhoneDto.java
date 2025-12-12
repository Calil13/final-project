package org.example.finalproject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.finalproject.validation.ValidPhone;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsersUpdatePhoneDto {
    @ValidPhone
    String phone;
}
