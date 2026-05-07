package com.ShopFlow.ShopFlow.entity;

import com.ShopFlow.ShopFlow.entity.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity // Entité JPA représentant une commande client
@Table(name = "orders") // Table "orders" en base de données
@Data // Getters/Setters via Lombok
@Builder // Pattern Builder
@NoArgsConstructor // Constructeur vide
@AllArgsConstructor // Constructeur plein
@EntityListeners(AuditingEntityListener.class) // Audit automatique
public class Order {

    @Id // Clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrément
    private Long id; // ID technique

    @Column(unique = true) // Numéro de commande unique
    private String orderNumber; // Référence lisible (ex: ORD-ABC123)

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToOne(fetch = FetchType.LAZY) // Relation vers l'utilisateur en mode LAZY pour les performances
    @JoinColumn(name = "customer_id") // Nom de la colonne de jointure
    private User customer; // Client qui a passé la commande

    @CreatedDate // Date gérée par Spring Data
    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime orderDate = LocalDateTime.now(); // Date de la commande

    @Enumerated(EnumType.STRING) // Stockage en chaîne de caractères
    private OrderStatus status; // État (PENDING, SHIPPED, DELIVERED, CANCELLED)

    private String shippingAddress; // Adresse de livraison saisie au checkout

    private Double subTotal; // Total hors frais de port
    private Double shippingFees; // Frais de livraison
    private Double discountAmount; // Montant de la remise appliquée
    private Double totalTTC; // Montant final payé par le client

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToOne(fetch = FetchType.LAZY) // Relation vers le coupon utilisé (LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon appliedCoupon; // Coupon appliqué à cette commande

    @JsonIgnore // Évite la sérialisation circulaire
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Liste des articles (LAZY)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>(); // Lignes de la commande (produits, quantités, prix unitaires)
}
