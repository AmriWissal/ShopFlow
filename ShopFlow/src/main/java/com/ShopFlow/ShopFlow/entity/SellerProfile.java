package com.ShopFlow.ShopFlow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity // Entité pour le profil détaillé d'un vendeur
@Getter // Génère Getters via Lombok
@Setter // Génère Setters via Lombok
@Builder // Pattern Builder
@NoArgsConstructor // Constructeur vide
@AllArgsConstructor // Constructeur plein
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Évite récursion infinie
@ToString(onlyExplicitlyIncluded = true) // Évite récursion infinie
public class SellerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id; // ID du profil vendeur

    @JsonIgnore // Évite la sérialisation circulaire
    @OneToOne(fetch = FetchType.LAZY) // Relation 1-1 avec l'utilisateur en mode LAZY
    @JoinColumn(name = "user_id")
    private User user; // Utilisateur propriétaire

    @ToString.Include
    private String shopName; // Nom de la boutique
    private String description; // Description de la boutique
    private String logo; // URL ou chemin du logo
    private Double note; // Note moyenne du vendeur
}
