package com.example.carsharingapp.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class UserUpdateProfileRequestDto {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Length(max = 30)
    private String firstName;
    @NotBlank
    @Length(max = 30)
    private String lastName;
}
