package com.ShopFlow.ShopFlow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity // Entité représentant un article dans le panier
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID de la ligne du panier

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToOne(fetch = FetchType.LAZY) // Relation vers le panier parent en mode LAZY
    @JoinColumn(name = "cart_id")
    private Cart cart; // Panier parent

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToOne(fetch = FetchType.LAZY) // Relation vers le produit en mode LAZY
    @JoinColumn(name = "product_id")
    private Product product; // Produit ajouté

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToOne(fetch = FetchType.LAZY) // Relation vers le variant en mode LAZY
    @JoinColumn(name = "variant_id")
    private ProductVariant variant; // Variant spécifique (taille, couleur...)

    private Integer quantity; // Quantité dans le panier

    /**
     * Méthode utilitaire pour calculer le prix total de la ligne.
     */
    public Double getTotalPrice() {
        if (product == null) return 0.0;
        Double unitPrice = product.getPrice(); // Prix de base
        if (variant != null && variant.getPriceDelta() != null) {
            unitPrice += variant.getPriceDelta(); // Ajout du surcoût du variant
        }
        return unitPrice * quantity; // Total = PrixUnitaire * Quantité
    }
}
