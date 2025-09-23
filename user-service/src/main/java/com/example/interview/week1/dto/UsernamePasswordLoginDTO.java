package com.example.interview.week1.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsernamePasswordLoginDTO {
    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
