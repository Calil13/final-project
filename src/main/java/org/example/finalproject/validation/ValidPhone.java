package org.example.finalproject.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPhoneValidator.class)
public @interface ValidPhone {

    String message() default "The phone number is not in the correct format!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
