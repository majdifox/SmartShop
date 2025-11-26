package org.majdifoxx.smartshop.service;

import org.majdifoxx.smartshop.dto.request.PaymentRequestDTO;
import org.majdifoxx.smartshop.dto.response.PaymentResponseDTO;
import org.majdifoxx.smartshop.enums.PaymentStatus;

import java.util.List;

public interface PaymentService {
    PaymentResponseDTO addPayment(Long orderId, PaymentRequestDTO request);
    PaymentResponseDTO updatePaymentStatus(Long paymentId, PaymentStatus newStatus);
    List<PaymentResponseDTO> getOrderPayments(Long orderId);
    List<PaymentResponseDTO> getPendingPayments();
    PaymentResponseDTO getPaymentById(Long id);
}
