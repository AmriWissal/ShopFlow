package com.ShopFlow.ShopFlow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity // Entité représentant un produit dans le catalogue
@Getter // Génère Getters via Lombok
@Setter // Génère Setters via Lombok
@Builder // Pattern Builder
@NoArgsConstructor // Constructeur vide
@AllArgsConstructor // Constructeur plein
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Évite récursion infinie
@ToString(onlyExplicitlyIncluded = true) // Évite récursion infinie
@EntityListeners(AuditingEntityListener.class) // Audit automatique
public class Product {

    @Id // Clé primaire
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrément
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id; // Identifiant technique

    @NotBlank // Champ obligatoire
    @ToString.Include
    private String name; // Nom du produit

    @Column(columnDefinition = "TEXT") // Type TEXT en base pour les descriptions longues
    private String description; // Description détaillée

    @NotNull // Champ obligatoire
    @ToString.Include
    private Double price; // Prix de base hors promotion

    private Double promoPrice; // Prix réduit (facultatif)

    @NotNull // Champ obligatoire
    @ToString.Include
    private Integer stock; // Quantité disponible en inventaire

    @Builder.Default // Valeur par défaut pour le Builder
    private boolean active = true; // État de visibilité du produit (soft delete)

    @CreatedDate // Date de création gérée par Spring
    @Column(updatable = false)
    private LocalDateTime createdAt; // Date de mise en ligne

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToOne(fetch = FetchType.LAZY) // Relation vers le vendeur en mode LAZY
    @JoinColumn(name = "seller_id")
    private User seller; // Vendeur propriétaire du produit

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToMany(fetch = FetchType.LAZY) // Relation N-N avec les catégories en mode LAZY
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>(); // Catégories auxquelles appartient le produit

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> images = new ArrayList<>(); // Galerie photos du produit

    @JsonIgnore // Évite la sérialisation circulaire
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Liste des variantes (LAZY)
    @Builder.Default
    private List<ProductVariant> variants = new ArrayList<>(); // Options (ex: Couleurs, Tailles)

    @JsonIgnore // Évite la sérialisation circulaire
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Liste des avis (LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>(); // Commentaires et notes des clients
}