package com.finance.dashboard.security;

import com.finance.dashboard.model.AppUser;
import com.finance.dashboard.model.UserStatus;
import com.finance.dashboard.repository.AppUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final AppUserRepository userRepository;

    public HeaderAuthenticationFilter(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String headerValue = request.getHeader(USER_ID_HEADER);
        if (headerValue == null || headerValue.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        Long userId;
        try {
            userId = Long.parseLong(headerValue);
        } catch (NumberFormatException exception) {
            throw new BadCredentialsException("X-User-Id must be a valid numeric user id");
        }

        Optional<AppUser> userOptional = userRepository.findById(userId);
        AppUser user = userOptional.orElseThrow(() -> new BadCredentialsException("User was not found for X-User-Id"));

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new DisabledException("The selected user is inactive");
        }

        FinanceUserPrincipal principal = FinanceUserPrincipal.from(user);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
