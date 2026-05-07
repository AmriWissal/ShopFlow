package com.ShopFlow.ShopFlow.entity;

import com.ShopFlow.ShopFlow.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity // Déclare la classe comme une entité JPA mappée sur une table
@Table(name = "users") // Nom de la table en base de données
@Getter // Génère Getters via Lombok
@Setter // Génère Setters via Lombok
@Builder // Permet d'utiliser le pattern Builder pour créer des instances
@NoArgsConstructor // Génère un constructeur sans arguments
@AllArgsConstructor // Génère un constructeur avec tous les arguments
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Évite récursion infinie
@ToString(onlyExplicitlyIncluded = true) // Évite récursion infinie
@EntityListeners(AuditingEntityListener.class) // Active l'audit automatique (ex: date de création)
public class User {

    @Id // Clé primaire de la table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrément par la base de données
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id; // Identifiant unique de l'utilisateur

    @Email // Valide le format de l'email
    @NotBlank // Champ obligatoire, ne peut pas être vide ou nul
    @Column(unique = true) // Contrainte d'unicité sur la colonne email
    @ToString.Include
    private String email; // Adresse email servant d'identifiant de connexion

    @NotBlank // Champ obligatoire
    private String password; // Mot de passe haché de l'utilisateur

    @NotBlank // Champ obligatoire
    @ToString.Include
    private String firstName; // Prénom de l'utilisateur

    @NotBlank // Champ obligatoire
    @ToString.Include
    private String lastName; // Nom de l'utilisateur

    @Enumerated(EnumType.STRING) // Stocke l'énumération sous forme de chaîne de caractères (ex: "ADMIN")
    @ToString.Include
    private Role role; // Rôle de l'utilisateur (ADMIN, SELLER, CUSTOMER)

    @Builder.Default // Valeur par défaut lors de l'utilisation du Builder
    private boolean active = true; // État du compte (activé par défaut)

    @CreatedDate // Date de création gérée automatiquement par Spring Data JPA
    @Column(updatable = false) // Empêche la modification de la date après création
    private LocalDateTime createdAt; // Date et heure de création du compte

    @JsonIgnore // Évite la sérialisation circulaire
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY) // Relation 1-1 avec le profil vendeur (mode LAZY)
    private SellerProfile sellerProfile; // Profil détaillé si l'utilisateur est un vendeur

    @JsonIgnore // Évite la sérialisation circulaire
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Relation 1-N avec les adresses (mode LAZY)
    @Builder.Default
    private List<Address> addresses = new ArrayList<>(); // Liste des adresses enregistrées par l'utilisateur

    /**
     * Retourne le nom complet de l'utilisateur.
     */
    public String getFullName() {
        if (firstName == null && lastName == null) return "Utilisateur ShopFlow";
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "").trim();
    }
}
