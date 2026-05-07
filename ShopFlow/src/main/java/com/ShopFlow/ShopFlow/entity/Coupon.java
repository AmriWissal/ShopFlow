package com.ShopFlow.ShopFlow.entity;

import com.ShopFlow.ShopFlow.entity.enums.CouponType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    private CouponType type;

    private Double value;
    private LocalDateTime expiryDate;
    private Integer maxUsage;
    @Builder.Default
    private Integer currentUsage = 0;
    @Builder.Default
    @Column(name = "active")
    private Boolean active = true;
    
    // Méthode helper pour compatibilité
    public boolean isActive() {
        return active != null && active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
}
