package org.majdifoxx.smartshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.majdifoxx.smartshop.enums.PaymentMethod;
import org.majdifoxx.smartshop.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponseDTO {

    private String id;
    private String orderId;
    private Integer paymentNumber;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String reference;
    private String bankName;
    private LocalDate dueDate;
    private LocalDateTime paymentDate;
    private LocalDateTime collectionDate;
    private LocalDateTime createdAt;
}
