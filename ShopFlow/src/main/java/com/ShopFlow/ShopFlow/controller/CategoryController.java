package com.ShopFlow.ShopFlow.controller;

import com.ShopFlow.ShopFlow.dto.response.CategoryResponse;
import com.ShopFlow.ShopFlow.entity.Category;
import com.ShopFlow.ShopFlow.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Déclare la classe comme contrôleur REST
@RequestMapping("/api/categories") // Point d'entrée principal pour les catégories
@RequiredArgsConstructor // Constructeur automatique pour les dépendances
@Tag(name = "Category", description = "Endpoints pour la gestion de l'arbre de catégories") // Doc Swagger
public class CategoryController {

    private final CategoryService categoryService; // Injection du service métier

    /**
     * Récupère l'arbre complet des catégories (Structure hiérarchique).
     */
    @GetMapping // Requête GET sur /api/categories
    @Operation(summary = "Récupérer l'arbre de catégories") // Description Swagger
    public ResponseEntity<List<CategoryResponse>> getCategoryTree() { // Retourne une liste de DTOs
        return ResponseEntity.ok(categoryService.getCategoryTree()); // Appelle le service pour l'arbre
    }

    /**
     * Crée une nouvelle catégorie. Réservé aux administrateurs.
     */
    @PostMapping // Requête POST sur /api/categories
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN')") // Sécurité : Seul l'admin peut créer
    @Operation(summary = "Créer une catégorie (ADMIN)") // Description Swagger
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody Category category) { // Corps de requête mappé sur l'entité
        return ResponseEntity.ok(categoryService.createCategory(category)); // Retourne la catégorie créée
    }

    /**
     * Met à jour une catégorie existante. Réservé aux administrateurs.
     */
    @PutMapping("/{id}") // Requête PUT sur /api/categories/{id}
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN')") // Sécurité : Seul l'admin peut modifier
    @Operation(summary = "Modifier une catégorie (ADMIN)") // Description Swagger
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @RequestBody Category category) { // Identifiant et nouvelles données
        return ResponseEntity.ok(categoryService.updateCategory(id, category)); // Retourne la catégorie mise à jour
    }

    /**
     * Supprime une catégorie. Réservé aux administrateurs.
     */
    @DeleteMapping("/{id}") // Requête DELETE sur /api/categories/{id}
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN')") // Sécurité : Seul l'admin peut supprimer
    @Operation(summary = "Supprimer une catégorie (ADMIN)") // Description Swagger
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) { // Identifiant de la catégorie
        categoryService.deleteCategory(id); // Appelle le service de suppression
        return ResponseEntity.noContent().build(); // Retourne un statut 204 No Content
    }
}