package com.finance.dashboard.service;

import com.finance.dashboard.dto.user.CreateUserRequest;
import com.finance.dashboard.dto.user.UpdateUserRequest;
import com.finance.dashboard.dto.user.UserResponse;
import com.finance.dashboard.exception.BadRequestException;
import com.finance.dashboard.exception.NotFoundException;
import com.finance.dashboard.model.AppUser;
import com.finance.dashboard.repository.AppUserRepository;
import com.finance.dashboard.security.FinanceUserPrincipal;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final AppUserRepository userRepository;

    public UserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(FinanceUserPrincipal principal) {
        AppUser user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new NotFoundException("Current user was not found"));
        return toResponse(user);
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        validateUniqueEmail(request.email(), null);
        AppUser user = new AppUser(
                request.fullName().trim(),
                request.email().trim().toLowerCase(),
                request.role(),
                request.status(),
                Instant.now()
        );
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request, FinanceUserPrincipal actingUser) {
        AppUser user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User was not found"));

        validateUniqueEmail(request.email(), id);
        if (actingUser.getId().equals(id) && request.status() != user.getStatus() && request.status().name().equals("INACTIVE")) {
            throw new BadRequestException("Admins cannot deactivate themselves");
        }

        user.setFullName(request.fullName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setRole(request.role());
        user.setStatus(request.status());
        return toResponse(user);
    }

    private void validateUniqueEmail(String email, Long currentUserId) {
        userRepository.findByEmailIgnoreCase(email.trim())
                .ifPresent(existingUser -> {
                    if (currentUserId == null || !existingUser.getId().equals(currentUserId)) {
                        throw new BadRequestException("A user with this email already exists");
                    }
                });
    }

    private UserResponse toResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
