package com.ShopFlow.ShopFlow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity // Entité JPA pour les catégories de produits
@Getter // Génère Getters via Lombok
@Setter // Génère Setters via Lombok
@Builder // Pattern Builder
@NoArgsConstructor // Constructeur vide
@AllArgsConstructor // Constructeur plein
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Évite récursion infinie
@ToString(onlyExplicitlyIncluded = true) // Évite récursion infinie
public class Category {

    @Id // Clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrément
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id; // Identifiant technique

    @NotBlank // Champ obligatoire
    @Column(unique = true) // Nom de catégorie unique
    @ToString.Include
    private String name; // Nom de la catégorie (ex: "Électronique")

    private String description; // Description facultative
    private String icon; // Nom ou classe de l'icône associée
    private String imageUrl; // URL de l'image de la catégorie (auto-générée si vide)

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToOne(fetch = FetchType.LAZY) // Relation vers la catégorie parente (mode LAZY)
    @JoinColumn(name = "parent_id") // Clé étrangère
    private Category parent; // Catégorie parente pour la hiérarchie

    @JsonIgnore // Évite la sérialisation circulaire
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Liste des sous-catégories (mode LAZY)
    @Builder.Default
    private List<Category> subCategories = new ArrayList<>(); // Enfants directs de cette catégorie

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToMany(mappedBy = "categories", fetch = FetchType.LAZY) // Relation N-N inverse avec les produits (mode LAZY)
    @Builder.Default
    private Set<Product> products = new HashSet<>(); // Liste des produits appartenant à cette catégorie

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToOne(fetch = FetchType.LAZY) // Relation vers le vendeur créateur (mode LAZY)
    @JoinColumn(name = "seller_id")
    private User seller; // Propriétaire de la catégorie (si créée par un vendeur)
}