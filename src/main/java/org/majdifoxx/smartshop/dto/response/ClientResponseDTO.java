package org.majdifoxx.smartshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.majdifoxx.smartshop.enums.CustomerTier;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO with full client details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientResponseDTO {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private CustomerTier tier;
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private LocalDateTime firstOrderDate;
    private LocalDateTime lastOrderDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
