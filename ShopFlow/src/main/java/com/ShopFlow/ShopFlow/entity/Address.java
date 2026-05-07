package com.ShopFlow.ShopFlow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity // Entité représentant une adresse postale
@Getter // Génère Getters via Lombok
@Setter // Génère Setters via Lombok
@Builder // Pattern Builder
@NoArgsConstructor // Constructeur vide
@AllArgsConstructor // Constructeur plein
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Évite récursion infinie
@ToString(onlyExplicitlyIncluded = true) // Évite récursion infinie
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id; // Identifiant de l'adresse

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToOne(fetch = FetchType.LAZY) // Relation vers l'utilisateur en mode LAZY
    @JoinColumn(name = "user_id")
    private User user; // Utilisateur à qui appartient l'adresse

    @ToString.Include
    private String street; // Rue
    @ToString.Include
    private String city; // Ville
    private String zipCode; // Code postal
    private String country; // Pays
    private boolean principal; // Indique si c'est l'adresse par défaut
}
