package com.finance.dashboard.security;

import com.finance.dashboard.model.AppUser;
import com.finance.dashboard.model.Role;
import com.finance.dashboard.model.UserStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class FinanceUserPrincipal {

    private final Long id;
    private final String fullName;
    private final String email;
    private final Role role;
    private final UserStatus status;

    public FinanceUserPrincipal(Long id, String fullName, String email, Role role, UserStatus status) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public static FinanceUserPrincipal from(AppUser user) {
        return new FinanceUserPrincipal(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus()
        );
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }
}
