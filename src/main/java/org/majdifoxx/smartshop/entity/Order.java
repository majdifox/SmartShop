package org.majdifoxx.smartshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.majdifoxx.smartshop.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "promo_code", length = 15)
    private String promoCode;

    // Pricing fields
    @Column(name = "subtotal_ht", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal subtotalHT = BigDecimal.ZERO;

    @Column(name = "loyalty_discount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal loyaltyDiscount = BigDecimal.ZERO;

    @Column(name = "promo_discount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal promoDiscount = BigDecimal.ZERO;

    @Column(name = "total_discount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalDiscount = BigDecimal.ZERO;

    @Column(name = "amount_ht", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal amountHT = BigDecimal.ZERO;

    @Column(name = "tva_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal tvaAmount = BigDecimal.ZERO;

    @Column(name = "total_ttc", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalTTC = BigDecimal.ZERO;

    @Column(name = "remaining_amount", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal remainingAmount = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

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
}
