package com.ShopFlow.ShopFlow.controller;

import com.ShopFlow.ShopFlow.dto.request.ProductRequest;
import com.ShopFlow.ShopFlow.dto.response.ProductResponse;
import com.ShopFlow.ShopFlow.security.CustomUserDetails;
import com.ShopFlow.ShopFlow.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Contrôleur REST pour les produits
@RequestMapping("/api/products") // Point d'entrée /api/products
@RequiredArgsConstructor // Injection par constructeur
@Tag(name = "Product", description = "Endpoints pour la gestion des produits, recherche et filtres") // Doc Swagger
public class ProductController {

    private final ProductService productService; // Service métier des produits

    /**
     * Récupère la liste des produits avec pagination et filtres optionnels.
     */
    @GetMapping // Requête GET sur /api/products
    @Operation(summary = "Liste paginée avec filtres (catégorie, prix, vendeur, promo)") // Description Swagger
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(required = false) String q, // Recherche par mot-clé
            @RequestParam(required = false) Long categoryId, // Filtre par catégorie
            @RequestParam(required = false) Double minPrice, // Prix minimum
            @RequestParam(required = false) Double maxPrice, // Prix maximum
            @RequestParam(required = false) Long sellerId, // Filtre par vendeur
            @RequestParam(defaultValue = "false") boolean promoOnly, // Uniquement produits en promotion
            @RequestParam(defaultValue = "0") int page, // Numéro de la page (défaut 0)
            @RequestParam(defaultValue = "10") int size) { // Taille de la page (défaut 10)
        
        Pageable pageable = PageRequest.of(page, size); // Prépare l'objet de pagination
        return ResponseEntity.ok(productService.getProducts(q, categoryId, minPrice, maxPrice, sellerId, promoOnly, pageable)); // Retourne les résultats filtrés
    }

    /**
     * Récupère les produits appartenant au vendeur connecté.
     */
    @GetMapping("/seller") // Requête GET sur /api/products/seller
    @PreAuthorize("hasAnyAuthority('SELLER','ADMIN')") // Sécurité : SELLER ou ADMIN
    @Operation(summary = "Liste des produits du vendeur connecté avec pagination") // Description Swagger
    public ResponseEntity<Page<ProductResponse>> getSellerProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page, // Numéro de la page (défaut 0)
            @RequestParam(defaultValue = "10") int size) { // Taille de la page (défaut 10)
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.getProductsBySellerPaginated(userDetails.getUser(), pageable)); // Retourne les produits du vendeur avec pagination
    }

    /**
     * Récupère les détails d'un produit spécifique, y compris variantes et avis.
     */
    @GetMapping("/{id}") // Requête GET sur /api/products/{id}
    @Operation(summary = "Détail + variantes + avis + note moyenne") // Description Swagger
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) { // Identifiant du produit
        return ResponseEntity.ok(productService.getProductById(id)); // Retourne le détail complet
    }

    /**
     * Permet à un vendeur ou admin de créer un nouveau produit.
     */
    @PostMapping // Requête POST sur /api/products
    @PreAuthorize("hasAnyAuthority('SELLER','ADMIN')") // Sécurité : SELLER ou ADMIN
    @Operation(summary = "Créer un produit (SELLER/ADMIN)") // Description Swagger
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody ProductRequest request, // Données du produit
            @AuthenticationPrincipal CustomUserDetails userDetails) { // Utilisateur créateur
        return ResponseEntity.ok(productService.createProduct(request, userDetails.getUser())); // Crée et retourne le produit
    }

    /**
     * Met à jour les informations d'un produit existant.
     */
    @PutMapping("/{id}") // Requête PUT sur /api/products/{id}
    @PreAuthorize("hasAnyAuthority('SELLER','ADMIN')") // Sécurité : Propriétaire ou ADMIN
    @Operation(summary = "Modifier un produit") // Description Swagger
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id, // ID du produit
            @RequestBody ProductRequest request, // Nouvelles données
            @AuthenticationPrincipal CustomUserDetails userDetails) { // Utilisateur effectuant la modif
        return ResponseEntity.ok(productService.updateProduct(id, request, userDetails.getUser())); // Met à jour
    }

    /**
     * Désactive un produit (suppression logique).
     */
    @DeleteMapping("/{id}") // Requête DELETE sur /api/products/{id}
    @PreAuthorize("hasAnyAuthority('SELLER','ADMIN')") // Sécurité : Propriétaire ou ADMIN
    @Operation(summary = "Désactiver un produit (soft delete)") // Description Swagger
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id, // ID du produit
            @AuthenticationPrincipal CustomUserDetails userDetails) { // Utilisateur
        productService.deleteProduct(id, userDetails.getUser()); // Désactive le produit
        return ResponseEntity.noContent().build(); // Retourne 204
    }

    /**
     * Recherche textuelle dans les noms et descriptions de produits.
     */
    @GetMapping("/search") // Requête GET sur /api/products/search
    @Operation(summary = "Recherche full-text") // Description Swagger
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String q) { // Mot-clé recherché
        return ResponseEntity.ok(productService.searchProducts(q)); // Retourne les matchs
    }

    /**
     * Récupère les 10 produits les plus vendus.
     */
    @GetMapping("/top-selling") // Requête GET sur /api/products/top-selling
    @Operation(summary = "Top 10 meilleures ventes") // Description Swagger
    public ResponseEntity<List<ProductResponse>> getTopSellingProducts() {
        return ResponseEntity.ok(productService.getTopSellingProducts()); // Retourne le top 10
    }
}