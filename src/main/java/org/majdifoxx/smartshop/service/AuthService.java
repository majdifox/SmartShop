package org.majdifoxx.smartshop.service;

import org.majdifoxx.smartshop.dto.request.LoginRequestDTO;
import org.majdifoxx.smartshop.dto.response.LoginResponseDTO;
import org.majdifoxx.smartshop.entity.User;

public interface AuthService {
    LoginResponseDTO login(LoginRequestDTO request);
    void logout();
    User getCurrentUser();
    boolean isAdmin();
}
