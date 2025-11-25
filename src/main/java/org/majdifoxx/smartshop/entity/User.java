package org.majdifoxx.smartshop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.majdifoxx.smartshop.enums.UserRole;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(length = 36)  // UUIDs are 36 characters (with hyphens)
    private String id;    // UUID stored as String as per requirements

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;  // Store hashed passwords in production!

    @Enumerated(EnumType.STRING)  // Store enum as "ADMIN" or "CLIENT" (not 0, 1)
    @Column(nullable = false)
    private UserRole role;

    /**
     * OneToOne relationship with Client
     * - A CLIENT user has one Client profile
     * - An ADMIN user has no Client profile (null)
     * - mappedBy: the "user" field in Client entity owns this relationship
     * - cascade: operations on User propagate to Client
     * - orphanRemoval: delete Client when User is deleted
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Client client;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * JPA lifecycle callback - runs before entity is persisted
     * Automatically generates UUID and sets creation timestamp
     */
    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        this.createdAt = LocalDateTime.now();
    }
}
