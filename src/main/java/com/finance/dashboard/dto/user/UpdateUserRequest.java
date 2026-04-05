package com.finance.dashboard.dto.user;

import com.finance.dashboard.model.Role;
import com.finance.dashboard.model.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank @Size(max = 100) String fullName,
        @NotBlank @Email @Size(max = 120) String email,
        @NotNull Role role,
        @NotNull UserStatus status
) {
}
