package com.ShopFlow.ShopFlow.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity // Entité représentant un avis client
@Getter // Génère Getters via Lombok
@Setter // Génère Setters via Lombok
@Builder // Pattern Builder
@NoArgsConstructor // Constructeur vide
@AllArgsConstructor // Constructeur plein
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Évite récursion infinie
@ToString(onlyExplicitlyIncluded = true) // Évite récursion infinie
@EntityListeners(AuditingEntityListener.class) // Permet la gestion automatique des dates
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id; // Identifiant de l'avis

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToOne(fetch = FetchType.LAZY) // Relation vers le client en mode LAZY
    @JoinColumn(name = "customer_id")
    private User customer; // Auteur de l'avis

    @JsonIgnore // Évite la sérialisation circulaire
    @ManyToOne(fetch = FetchType.LAZY) // Relation vers le produit en mode LAZY
    @JoinColumn(name = "product_id")
    private Product product; // Produit concerné

    @Min(1)
    @Max(5)
    @ToString.Include
    private Integer note; // Note de 1 à 5

    @ToString.Include
    private String comment; // Commentaire texte

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt; // Date de création automatique

    @Builder.Default
    private boolean approved = false; // Statut de validation par l'admin
}
