package org.majdifoxx.smartshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.majdifoxx.smartshop.enums.UserRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private Long userId;
    private String username;
    private UserRole role;
    private Long clientId;   // null for ADMIN
    private String message;
}
