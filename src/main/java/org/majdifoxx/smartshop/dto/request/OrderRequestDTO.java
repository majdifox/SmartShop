package org.majdifoxx.smartshop.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDTO {

    @NotNull
    private Long clientId;

    @NotEmpty
    private List<OrderItemRequestDTO> items;

    @Pattern(regexp = "^PROMO-[A-Z0-9]{4}$", message = "Promo code must match PROMO-XXXX")
    private String promoCode;
}
