package org.majdifoxx.smartshop.service;

import lombok.RequiredArgsConstructor;
import org.majdifoxx.smartshop.dto.request.PaymentRequestDTO;
import org.majdifoxx.smartshop.dto.response.PaymentResponseDTO;
import org.majdifoxx.smartshop.entity.Order;
import org.majdifoxx.smartshop.entity.Payment;
import org.majdifoxx.smartshop.enums.PaymentMethod;
import org.majdifoxx.smartshop.enums.PaymentStatus;
import org.majdifoxx.smartshop.exception.BusinessRuleException;
import org.majdifoxx.smartshop.exception.ResourceNotFoundException;
import org.majdifoxx.smartshop.mapper.PaymentMapper;
import org.majdifoxx.smartshop.repository.OrderRepository;
import org.majdifoxx.smartshop.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private static final BigDecimal CASH_LIMIT = new BigDecimal("20000");

    @Override
    @Transactional
    public PaymentResponseDTO addPayment(Long orderId, PaymentRequestDTO request) {
        // Find order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        // IMPORTANT: Check CASH limit FIRST (before remaining amount check)
        if (request.getPaymentMethod() == PaymentMethod.CASH) {
            if (request.getAmount().compareTo(CASH_LIMIT) > 0) {
                throw new BusinessRuleException(
                        "Cash payment exceeds legal limit of 20,000 DH (Art. 193 CGI). " +
                                "Amount: " + request.getAmount() + " DH");
            }
        }

        // Validate payment amount doesn't exceed remaining amount
        if (request.getAmount().compareTo(order.getRemainingAmount()) > 0) {
            throw new BusinessRuleException(
                    "Payment amount (" + request.getAmount() + " DH) exceeds remaining amount ("
                            + order.getRemainingAmount() + " DH)");
        }

        // Calculate next payment number (sequential)
        int nextPaymentNumber = order.getPayments().size() + 1;

        // Create payment entity
        Payment payment = Payment.builder()
                .order(order)
                .paymentNumber(nextPaymentNumber)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.EN_ATTENTE)
                .reference(request.getReference())
                .bankName(request.getBankName())
                .dueDate(request.getDueDate())
                .paymentDate(LocalDateTime.now())
                .build();

        // Save payment
        Payment savedPayment = paymentRepository.save(payment);

        // Update order remaining amount
        BigDecimal newRemainingAmount = order.getRemainingAmount().subtract(request.getAmount());
        order.setRemainingAmount(newRemainingAmount);
        orderRepository.save(order);

        return paymentMapper.toResponseDTO(savedPayment);
    }

    @Override
    public PaymentResponseDTO updatePaymentStatus(Long paymentId, PaymentStatus newStatus) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        payment.setStatus(newStatus);

        // Set collection date when status becomes COLLECTED
        if (newStatus == PaymentStatus.ENCAISSE  && payment.getCollectionDate() == null) {
            payment.setCollectionDate(LocalDateTime.now());
        }

        Payment updated = paymentRepository.save(payment);
        return paymentMapper.toResponseDTO(updated);
    }

    @Override
    public List<PaymentResponseDTO> getOrderPayments(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderIdOrderByPaymentNumberAsc(orderId);
        return payments.stream()
                .map(paymentMapper::toResponseDTO)
                .toList();
    }

    @Override
    public List<PaymentResponseDTO> getPendingPayments() {
        List<Payment> payments = paymentRepository.findByStatus(PaymentStatus.EN_ATTENTE);
        return payments.stream()
                .map(paymentMapper::toResponseDTO)
                .toList();
    }

    @Override
    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return paymentMapper.toResponseDTO(payment);
    }
}
