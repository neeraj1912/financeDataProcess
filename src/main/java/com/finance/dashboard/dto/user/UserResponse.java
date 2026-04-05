package com.finance.dashboard.dto.user;

import com.finance.dashboard.model.Role;
import com.finance.dashboard.model.UserStatus;
import java.time.Instant;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        Role role,
        UserStatus status,
        Instant createdAt
) {
}
