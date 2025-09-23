package com.example.interview.week1.dto;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrationDTO {
    @NotBlank(message = "Username cannot be blank")
    @Length(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    @Length(min = 8, max = 16, message = "Password must be between 8 and 16 characters")
    private String password;
}
