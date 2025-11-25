package org.majdifoxx.smartshop.entity;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

import lombok.*;
import org.majdifoxx.smartshop.enums.CustomerTier;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String address;

    /**
     * CustomerTier: le niveau de fidélité du client (Basic Par defaut)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default  // Sets default value when using builder pattern
    private CustomerTier tier = CustomerTier.BASIC;

    /**
     * Statistics tracked automatically
     */
    @Column(name = "total_orders", nullable = false)
    @Builder.Default
    private Integer totalOrders = 0;

    @Column(name = "total_spent", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(name = "first_order_date")
    private LocalDateTime firstOrderDate;

    @Column(name = "last_order_date")
    private LocalDateTime lastOrderDate;

    /**
     * OneToOne relationship with User
     * - This is the owning side (has the foreign key)
     * - @JoinColumn creates "user_id" column in clients table
     * - optional = false: every Client MUST have a User
     */
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * OneToMany relationship with Orders
     * - A client has many orders
     * - mappedBy: the "client" field in Order entity owns this
     * - cascade = ALL: deleting client deletes all orders (be careful!)
     * - orphanRemoval: delete order if removed from this list
     */
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

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
     * Business logic helper method
     * Updates client statistics after a confirmed order
     */
    public void updateStats(BigDecimal orderAmount) {
        this.totalOrders++;
        this.totalSpent = this.totalSpent.add(orderAmount);
        this.lastOrderDate = LocalDateTime.now();

        if (this.firstOrderDate == null) {
            this.firstOrderDate = LocalDateTime.now();
        }
    }
}
