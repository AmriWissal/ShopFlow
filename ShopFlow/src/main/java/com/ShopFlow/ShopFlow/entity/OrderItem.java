package com.ShopFlow.ShopFlow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity // Entité représentant une ligne de commande
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identifiant unique de la ligne

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToOne(fetch = FetchType.LAZY) // Relation vers la commande en mode LAZY
    @JoinColumn(name = "order_id")
    private Order order; // Commande parente

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToOne(fetch = FetchType.LAZY) // Relation vers le produit en mode LAZY
    @JoinColumn(name = "product_id")
    private Product product; // Produit acheté

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToOne(fetch = FetchType.LAZY) // Relation vers le variant en mode LAZY
    @JoinColumn(name = "variant_id")
    private ProductVariant variant; // Option spécifique du produit (ex: Taille L)

    private Integer quantity; // Quantité achetée
    private Double unitPrice; // Prix unitaire au moment de la commande
    private Double totalPrice; // Prix total de la ligne (quantité * unitPrice)
}
