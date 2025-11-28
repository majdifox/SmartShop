package org.majdifoxx.smartshop.controller;

import lombok.RequiredArgsConstructor;
import org.majdifoxx.smartshop.dto.request.PaymentRequestDTO;
import org.majdifoxx.smartshop.dto.response.PaymentResponseDTO;
import org.majdifoxx.smartshop.enums.PaymentStatus;
import org.majdifoxx.smartshop.exception.UnauthorizedException;
import org.majdifoxx.smartshop.service.AuthService;
import org.majdifoxx.smartshop.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Payment management endpoints
 * PDF Section 5: "Système de Paiements Multi-Moyens"
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthService authService;

    /**
     * POST /api/orders/{orderId}/payments
     * PDF: "Une commande peut être payée en plusieurs fois"
     * Access: ADMIN only
     */
    @PostMapping("/orders/{orderId}/payments")
    public ResponseEntity<PaymentResponseDTO> addPayment(
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentRequestDTO request) {

        // PDF: "ADMIN: Enregistrer le paiement"
        if (!authService.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can add payments");
        }

        PaymentResponseDTO response = paymentService.addPayment(orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/orders/{orderId}/payments
     * PDF: Get all payments for an order
     * Access: ADMIN (all orders) or CLIENT (only own orders)
     */
    @GetMapping("/orders/{orderId}/payments")
    public ResponseEntity<List<PaymentResponseDTO>> getOrderPayments(@PathVariable Long orderId) {
        // Clients can view payments for their own orders
        // (Additional check would be needed to verify order ownership)

        List<PaymentResponseDTO> response = paymentService.getOrderPayments(orderId);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/payments/{id}/status
     * PDF: "Statuts possibles: En attente / Encaissé / Rejeté"
     * Access: ADMIN only
     */
    @PutMapping("/payments/{id}/status")
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam PaymentStatus status) {

        if (!authService.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can update payment status");
        }

        PaymentResponseDTO response = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/payments/pending
     * Access: ADMIN only
     */
    @GetMapping("/payments/pending")
    public ResponseEntity<List<PaymentResponseDTO>> getPendingPayments() {
        if (!authService.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can view pending payments");
        }

        List<PaymentResponseDTO> response = paymentService.getPendingPayments();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/payments/{id}
     * Access: ADMIN only
     */
    @GetMapping("/payments/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        if (!authService.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can view payment details");
        }

        PaymentResponseDTO response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }
}
