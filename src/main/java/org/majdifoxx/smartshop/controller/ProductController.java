package org.majdifoxx.smartshop.controller;

import lombok.RequiredArgsConstructor;
import org.majdifoxx.smartshop.dto.request.ProductRequestDTO;
import org.majdifoxx.smartshop.dto.response.ProductResponseDTO;
import org.majdifoxx.smartshop.exception.UnauthorizedException;
import org.majdifoxx.smartshop.service.AuthService;
import org.majdifoxx.smartshop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Product management endpoints
 * PDF Section 3: "Gestion des Produits"
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final AuthService authService;

    /**
     * POST /api/products
     * PDF: "Ajouter des produits"
     * Access: ADMIN only
     */
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductRequestDTO request) {
        // PDF: "CLIENT NE PEUT PAS créer"
        if (!authService.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can create products");
        }

        ProductResponseDTO response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/products/{id}
     * PDF: "Consulter la liste des produits"
     * Access: All authenticated users
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        ProductResponseDTO response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/products
     * PDF: "Consulter la liste des produits avec filtres et pagination"
     * Access: All authenticated users (CLIENT: lecture seule)
     */
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> getProducts(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponseDTO> response = productService.getActiveProducts(search, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/products/{id}
     * PDF: "Modifier les informations produits"
     * Access: ADMIN only
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO request) {

        // PDF: "CLIENT NE PEUT PAS modifier"
        if (!authService.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can update products");
        }

        ProductResponseDTO response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/products/{id}
     * PDF: "Supprimer des produits (soft delete)"
     * Access: ADMIN only
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        // PDF: "CLIENT NE PEUT PAS supprimer"
        if (!authService.isAdmin()) {
            throw new UnauthorizedException("Only ADMIN can delete products");
        }

        productService.deleteProduct(id);
        return ResponseEntity.ok("Product soft deleted successfully");
    }
}
