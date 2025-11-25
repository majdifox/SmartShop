package org.majdifoxx.smartshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents sellable products with soft-delete capability
 * Soft delete: marked as deleted but kept in DB for order history
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    /**
     * Unit price (HT - Hors Taxes / Before Tax)
     */
    @Min(value = 0, message = "Price must be positive")
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Available stock quantity
     */
    @Min(value = 0, message = "Stock cannot be negative")
    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    /**
     * Soft delete flag
     * If true: product hidden from listings but visible in old orders
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: check if sufficient stock available
     */
    public boolean hasStock(Integer requestedQuantity) {
        return !this.deleted && this.stock >= requestedQuantity;
    }

    /**
     * Business logic: decrease stock after order confirmation
     */
    public void decrementStock(Integer quantity) {
        if (this.stock < quantity) {
            throw new IllegalStateException("Insufficient stock for product: " + this.name);
        }
        this.stock -= quantity;
    }
}
