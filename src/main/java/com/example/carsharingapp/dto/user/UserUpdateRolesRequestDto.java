package com.example.carsharingapp.dto.user;

import com.example.carsharingapp.model.Role;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import lombok.Data;

@Data
public class UserUpdateRolesRequestDto {
    @NotEmpty
    private Set<Role.RoleName> roles;
}
