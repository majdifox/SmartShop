package org.majdifoxx.smartshop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Request body for creating orders
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    @NotBlank(message = "Client ID is required")
    private String clientId;

    @NotEmpty(message = "Order must have at least one item")
    private List<OrderItemRequest> items;

    @Pattern(regexp = "^PROMO-[A-Z0-9]{4}$", message = "Invalid promo code format (must be PROMO-XXXX)")
    private String promoCode;

    /**
     * Nested DTO for order items
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        @NotBlank(message = "Product ID is required")
        private String productId;

        @jakarta.validation.constraints.Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;
    }
}
