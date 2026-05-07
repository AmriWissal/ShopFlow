package com.ShopFlow.ShopFlow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity // Entité représentant une variante d'un produit
@Getter // Génère Getters via Lombok
@Setter // Génère Setters via Lombok
@Builder // Pattern Builder
@NoArgsConstructor // Constructeur vide
@AllArgsConstructor // Constructeur plein
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Évite récursion infinie
@ToString(onlyExplicitlyIncluded = true) // Évite récursion infinie
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id; // ID de la variante

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToOne(fetch = FetchType.LAZY) // Relation vers le produit parent en mode LAZY
    @JoinColumn(name = "product_id")
    private Product product; // Produit parent

    @ToString.Include
    private String attribute; // Type d'attribut (ex: "Taille", "Couleur")
    @ToString.Include
    private String value;     // Valeur de l'attribut (ex: "L", "Bleu")
    private Integer extraStock; // Stock spécifique à cette variante
    private Double priceDelta; // Différence de prix (+/-) par rapport au prix de base
}
