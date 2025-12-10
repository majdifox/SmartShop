package org.majdifoxx.smartshop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "promo_codes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    @Pattern(regexp = "^PROMO-[A-Z0-9]{4}$")
    private String code;

    @Builder.Default
    @Column(nullable = false)
    private Boolean used = false;

    private LocalDateTime usedAt;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
