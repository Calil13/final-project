package org.example.finalproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.finalproject.validation.ValidEmail;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailRequest {
    @ValidEmail
    private String email;
}
