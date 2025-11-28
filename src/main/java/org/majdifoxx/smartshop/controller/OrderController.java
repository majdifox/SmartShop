package org.majdifoxx.smartshop.controller;

import lombok.RequiredArgsConstructor;
import org.majdifoxx.smartshop.dto.request.OrderRequestDTO;
import org.majdifoxx.smartshop.dto.response.OrderResponseDTO;
import org.majdifoxx.smartshop.exception.UnauthorizedException;
import org.majdifoxx.smartshop.service.AuthService;
import org.majdifoxx.smartshop.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Order management endpoints
 * PDF Section 4: "Gestion des Commandes"
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final AuthService authService;

    /**
     * POST /api/orders
     * PDF: "Créer une commande multi-produits avec quantités"
     * Access: ADMIN only (creates orders for any client)
     */
    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO request) {
        // PDF: "ADMIN: Créer des commandes pour n'importe quel client"
        if (!authService.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can create orders");
        }

        OrderResponseDTO response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/orders/{id}
     * Access: ADMIN (all orders) or CLIENT (only own orders)
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        OrderResponseDTO order = orderService.getOrderById(id);

        if (!authService.isAdmin()) {
            // Verify client can only see their own orders
            Long currentClientId = authService.getCurrentUser().getClient().getId();
            if (!currentClientId.equals(order.getClientId())) {
                throw new UnauthorizedException("Clients can only view their own orders");
            }
        }

        return ResponseEntity.ok(order);
    }

    /**
     * GET /api/orders
     * Access: ADMIN only
     */
    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        if (!authService.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can view all orders");
        }

        List<OrderResponseDTO> response = orderService.getAllOrders();
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/orders/{id}/confirm
     * PDF: "PENDING → CONFIRMED: validation par ADMIN (après paiement complet)"
     * Access: ADMIN only
     */
    @PutMapping("/{id}/confirm")
    public ResponseEntity<OrderResponseDTO> confirmOrder(@PathVariable Long id) {
        // PDF: "ADMIN: Valider les commandes"
        if (!authService.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can confirm orders");
        }

        OrderResponseDTO response = orderService.confirmOrder(id);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/orders/{id}/cancel
     * PDF: "PENDING → CANCELED: annulation par ADMIN"
     * Access: ADMIN only
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long id) {
        // PDF: "ADMIN: Annuler les commandes"
        if (!authService.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can cancel orders");
        }

        OrderResponseDTO response = orderService.cancelOrder(id);
        return ResponseEntity.ok(response);
    }
}
