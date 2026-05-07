package com.ShopFlow.ShopFlow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore // Évite la sérialisation circulaire
    @OneToOne(fetch = FetchType.LAZY) // Relation un-à-un avec l'utilisateur en mode LAZY
    @JoinColumn(name = "customer_id") // Clé étrangère vers la table des utilisateurs
    private User customer; // Propriétaire du panier

    @JsonIgnore // Évite la sérialisation circulaire
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon appliedCoupon;

    @Builder.Default
    private Double totalPrice = 0.0;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
