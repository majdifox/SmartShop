package org.majdifoxx.smartshop.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.majdifoxx.smartshop.dto.request.LoginRequestDTO;
import org.majdifoxx.smartshop.dto.response.LoginResponseDTO;
import org.majdifoxx.smartshop.entity.User;
import org.majdifoxx.smartshop.enums.UserRole;
import org.majdifoxx.smartshop.exception.UnauthorizedException;
import org.majdifoxx.smartshop.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Store in session
        httpSession.setAttribute("userId", user.getId());

        // FIX: Only get clientId if user is CLIENT
        Long clientId = null;
        if (user.getRole() == UserRole.CLIENT && user.getClient() != null) {
            clientId = user.getClient().getId();
        }

        return LoginResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .clientId(clientId)  // ← Will be null for ADMIN, that's OK
                .message("Login successful")
                .build();
    }


    @Override
    public void logout() {
        httpSession.invalidate();
    }

    @Override
    public User getCurrentUser() {
        Long userId = (Long) httpSession.getAttribute("userId");
        if (userId == null) {
            throw new UnauthorizedException("Not authenticated. Please login.");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("Session invalid. Please login again."));
    }

    @Override
    public boolean isAdmin() {
        User user = getCurrentUser();
        return user.getRole() == UserRole.ADMIN;
    }
}
