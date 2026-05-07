package com.ShopFlow.ShopFlow.controller;

import com.ShopFlow.ShopFlow.dto.request.CartItemRequest;
import com.ShopFlow.ShopFlow.dto.response.CartResponse;
import com.ShopFlow.ShopFlow.security.CustomUserDetails;
import com.ShopFlow.ShopFlow.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController // Contrôleur REST pour le panier
@RequestMapping("/api/cart") // Route de base /api/cart
@RequiredArgsConstructor // Constructeur pour l'injection
@Tag(name = "Cart", description = "Endpoints pour la gestion du panier et des coupons") // Doc Swagger
public class CartController {

    private final CartService cartService; // Service du panier

    /**
     * Récupère le panier du client connecté.
     */
    @GetMapping // Requête GET sur /api/cart
    @Operation(summary = "Panier du client connecté") // Description Swagger
    public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal CustomUserDetails userDetails) { // Récupère l'utilisateur via le token
        return ResponseEntity.ok(cartService.getCartResponse(userDetails.getUser())); // Retourne le panier converti
    }

    /**
     * Ajoute un article au panier.
     */
    @PostMapping("/items") // Requête POST sur /api/cart/items
    @Operation(summary = "Ajouter un article") // Description Swagger
    public ResponseEntity<CartResponse> addItemToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails, // Utilisateur authentifié
            @Valid @RequestBody CartItemRequest request) { // Valide l'article à ajouter
        return ResponseEntity.ok(cartService.addItemToCart(userDetails.getUser(), request.getProductId(), request.getQuantity())); // Ajoute et retourne le panier
    }

    /**
     * Modifie la quantité d'un article dans le panier.
     */
    @PutMapping("/items/{itemId}") // Requête PUT sur /api/cart/items/{itemId}
    @Operation(summary = "Modifier quantité") // Description Swagger
    public ResponseEntity<CartResponse> updateItemQuantity(
            @AuthenticationPrincipal CustomUserDetails userDetails, // Utilisateur authentifié
            @PathVariable Long itemId, // Identifiant de l'article dans le panier
            @RequestParam Integer quantity) { // Nouvelle quantité
        return ResponseEntity.ok(cartService.updateItemQuantity(userDetails.getUser(), itemId, quantity)); // Met à jour et retourne le panier
    }

    /**
     * Retire un article du panier.
     */
    @DeleteMapping("/items/{itemId}") // Requête DELETE sur /api/cart/items/{itemId}
    @Operation(summary = "Retirer un article") // Description Swagger
    public ResponseEntity<CartResponse> removeItemFromCart(
            @AuthenticationPrincipal CustomUserDetails userDetails, // Utilisateur authentifié
            @PathVariable Long itemId) { // Identifiant de l'article à supprimer
        return ResponseEntity.ok(cartService.removeItemFromCart(userDetails.getUser(), itemId)); // Supprime et retourne le panier
    }

    /**
     * Applique un code promo au panier.
     */
    @PostMapping("/coupon") // Requête POST sur /api/cart/coupon
    @Operation(summary = "Appliquer un code promo") // Description Swagger
    public ResponseEntity<CartResponse> applyCoupon(
            @AuthenticationPrincipal CustomUserDetails userDetails, // Utilisateur authentifié
            @RequestParam String code) { // Code promo saisi
        return ResponseEntity.ok(cartService.applyCoupon(userDetails.getUser(), code)); // Applique le coupon et retourne le panier
    }

    /**
     * Retire le code promo du panier.
     */
    @DeleteMapping("/coupon") // Requête DELETE sur /api/cart/coupon
    @Operation(summary = "Retirer le code promo") // Description Swagger
    public ResponseEntity<CartResponse> removeCoupon(
            @AuthenticationPrincipal CustomUserDetails userDetails) { // Utilisateur authentifié
        return ResponseEntity.ok(cartService.removeCoupon(userDetails.getUser())); // Retire le coupon et retourne le panier
    }
}