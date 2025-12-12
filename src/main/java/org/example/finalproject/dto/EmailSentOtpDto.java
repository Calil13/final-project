package org.example.finalproject.dto;

import lombok.Data;
import org.example.finalproject.validation.ValidEmail;

@Data
public class EmailSentOtpDto {
    @ValidEmail
    private String email;
}