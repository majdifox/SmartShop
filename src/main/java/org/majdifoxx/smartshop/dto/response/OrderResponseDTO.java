package org.majdifoxx.smartshop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.majdifoxx.smartshop.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    private Long id;
    private Long clientId;
    private String clientName;
    private OrderStatus status;
    private String promoCode;

    private BigDecimal subtotalHT;
    private BigDecimal loyaltyDiscount;
    private BigDecimal promoDiscount;
    private BigDecimal totalDiscount;
    private BigDecimal amountHT;
    private BigDecimal tvaAmount;
    private BigDecimal totalTTC;
    private BigDecimal remainingAmount;

    private List<OrderItemResponseDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
