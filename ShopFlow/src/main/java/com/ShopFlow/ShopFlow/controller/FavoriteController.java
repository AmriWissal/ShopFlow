package com.ShopFlow.ShopFlow.controller;

import com.ShopFlow.ShopFlow.dto.response.FavoriteResponse;
import com.ShopFlow.ShopFlow.security.CustomUserDetails;
import com.ShopFlow.ShopFlow.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Endpoints pour la gestion des favoris")
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * Récupère tous les favoris de l'utilisateur connecté
     */
    @GetMapping
    @Operation(summary = "Liste des favoris de l'utilisateur")
    public ResponseEntity<List<FavoriteResponse>> getUserFavorites(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(favoriteService.getUserFavorites(userDetails.getUser()));
    }

    /**
     * Vérifie si un produit est dans les favoris
     */
    @GetMapping("/check/{productId}")
    @Operation(summary = "Vérifier si un produit est favori")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isFavorite = favoriteService.isFavorite(userDetails.getUser(), productId);
        return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
    }

    /**
     * Toggle un produit dans les favoris (ajoute ou retire)
     */
    @PostMapping("/{productId}/toggle")
    @Operation(summary = "Ajouter/Retirer un produit des favoris")
    public ResponseEntity<Map<String, Boolean>> toggleFavorite(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isFavorite = favoriteService.toggleFavorite(userDetails.getUser(), productId);
        return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
    }

    /**
     * Ajoute un produit aux favoris
     */
    @PostMapping("/{productId}")
    @Operation(summary = "Ajouter aux favoris")
    public ResponseEntity<FavoriteResponse> addToFavorites(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(favoriteService.addToFavorites(userDetails.getUser(), productId));
    }

    /**
     * Retire un produit des favoris
     */
    @DeleteMapping("/{productId}")
    @Operation(summary = "Retirer des favoris")
    public ResponseEntity<Void> removeFromFavorites(
            @PathVariable Long productId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        favoriteService.removeFromFavorites(userDetails.getUser(), productId);
        return ResponseEntity.noContent().build();
    }
}
