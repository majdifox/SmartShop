package org.majdifoxx.smartshop.controller;

import lombok.RequiredArgsConstructor;
import org.majdifoxx.smartshop.dto.request.ClientRequestDTO;
import org.majdifoxx.smartshop.dto.response.ClientResponseDTO;
import org.majdifoxx.smartshop.dto.response.OrderResponseDTO;
import org.majdifoxx.smartshop.exception.UnauthorizedException;
import org.majdifoxx.smartshop.service.AuthService;
import org.majdifoxx.smartshop.service.ClientService;
import org.majdifoxx.smartshop.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Client management endpoints
 * PDF Section 1: "Gestion des Clients"
 */
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final OrderService orderService;
    private final AuthService authService;

    /**
     * POST /api/clients
     * PDF: "Créer un client"
     * Access: ADMIN only
     */
    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(@Valid @RequestBody ClientRequestDTO request) {
        // PDF: "ADMIN peut tout faire: Toutes les opérations (CRUD complet)"
        if (!authService.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can create clients");
        }

        ClientResponseDTO response = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/clients/{id}
     * PDF: "Consulter les informations d'un client"
     * Access: ADMIN (all clients) or CLIENT (only own data)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getClientById(@PathVariable Long id) {
        if (!authService.isAdmin()) {
            // PDF: "CLIENT peut uniquement: Consulter SES PROPRES données"
            // PDF: "NE PEUT PAS voir les données des autres clients"
            Long currentClientId = authService.getCurrentUser().getClient().getId();
            if (!currentClientId.equals(id)) {
                throw new UnauthorizedException("Clients can only view their own data");
            }
        }

        ClientResponseDTO response = clientService.getClientById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/clients
     * PDF: "Voir tous les clients"
     * Access: ADMIN only
     */
    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getAllClients() {
        // PDF: "ADMIN peut tout faire: Voir tous les clients"
        if (!authService.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can view all clients");
        }

        List<ClientResponseDTO> response = clientService.getAllClients();
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/clients/{id}
     * PDF: "Mettre à jour les données d'un client"
     * Access: ADMIN only
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequestDTO request) {

        // PDF: "ADMIN peut tout faire: CRUD complet"
        if (!authService.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can update clients");
        }

        ClientResponseDTO response = clientService.updateClient(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/clients/{id}
     * PDF: "Suppression d'un client"
     * Access: ADMIN only
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClient(@PathVariable Long id) {
        // PDF: "ADMIN peut tout faire: CRUD complet"
        if (!authService.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can delete clients");
        }

        clientService.deleteClient(id);
        return ResponseEntity.ok("Client deleted successfully");
    }

    /**
     * GET /api/clients/{id}/orders
     * PDF: "Consulter l'historique des commandes"
     * Access: ADMIN (all clients) or CLIENT (only own orders)
     */
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponseDTO>> getClientOrders(@PathVariable Long id) {
        if (!authService.isAdmin()) {
            // PDF: "CLIENT peut uniquement: Historique des commandes"
            Long currentClientId = authService.getCurrentUser().getClient().getId();
            if (!currentClientId.equals(id)) {
                throw new UnauthorizedException("Clients can only view their own orders");
            }
        }

        List<OrderResponseDTO> response = orderService.getClientOrders(id);
        return ResponseEntity.ok(response);
    }
}
