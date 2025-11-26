package org.majdifoxx.smartshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.majdifoxx.smartshop.enums.UserRole;

/**
 * Response after successful login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private String userId;
    private String username;
    private UserRole role;
    private String clientId;  // null for ADMIN users
    private String message;
}
