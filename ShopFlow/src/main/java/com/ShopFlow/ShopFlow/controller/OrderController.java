package com.ShopFlow.ShopFlow.controller;

import com.ShopFlow.ShopFlow.dto.request.OrderRequest;
import com.ShopFlow.ShopFlow.dto.response.OrderResponse;
import com.ShopFlow.ShopFlow.entity.enums.OrderStatus;
import com.ShopFlow.ShopFlow.security.CustomUserDetails;
import com.ShopFlow.ShopFlow.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Déclare le contrôleur REST
@RequestMapping("/api/orders") // Route principale /api/orders
@RequiredArgsConstructor // Constructeur pour l'injection
@Tag(name = "Order", description = "Endpoints pour la gestion des commandes") // Doc Swagger
public class OrderController {

    private final OrderService orderService; // Service métier des commandes

    /**
     * Passe une commande à partir du panier actuel.
     */
    @PostMapping // Requête POST sur /api/orders
    @Operation(summary = "Passer une commande depuis le panier") // Description Swagger
    public ResponseEntity<OrderResponse> placeOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails, // Client authentifié
            @Valid @RequestBody OrderRequest request) { // Données de livraison
        try {
            return ResponseEntity.ok(orderService.placeOrder(userDetails.getUser(), request)); // Crée la commande
        } catch (RuntimeException e) {
            // Log l'erreur pour le débogage
            System.err.println("Erreur lors de la création de la commande: " + e.getMessage());
            e.printStackTrace();
            throw e; // Relance l'exception pour que Spring la gère
        }
    }

    /**
     * Récupère les détails d'une commande par son ID.
     */
    @GetMapping("/{id}") // Requête GET sur /api/orders/{id}
    @Operation(summary = "Détail d'une commande") // Description Swagger
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) { // ID de la commande
        return ResponseEntity.ok(orderService.getOrderResponseById(id)); // Retourne les détails
    }

    /**
     * Récupère l'historique des commandes du client connecté.
     */
    @GetMapping("/my") // Requête GET sur /api/orders/my
    @Operation(summary = "Commandes du client connecté") // Description Swagger
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal CustomUserDetails userDetails) { // Client authentifié
        return ResponseEntity.ok(orderService.getMyOrders(userDetails.getUser())); // Retourne la liste
    }

    /**
     * Met à jour le statut d'une commande. Accessible aux vendeurs et administrateurs.
     */
    @PutMapping("/{id}/status") // Requête PUT sur /api/orders/{id}/status
    @PreAuthorize("hasAnyAuthority('SELLER','ADMIN')") // Sécurité : SELLER ou ADMIN
    @Operation(summary = "Mettre à jour le statut (SELLER/ADMIN)") // Description Swagger
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id, // ID commande
            @RequestParam OrderStatus status) { // Nouveau statut (ex: EXPÉDIÉE)
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status)); // Applique le changement
    }

    /**
     * Annule une commande. Réservé au client si la commande est encore éligible.
     */
    @PutMapping("/{id}/cancel") // Requête PUT sur /api/orders/{id}/cancel
    @Operation(summary = "Annuler une commande (CUSTOMER si éligible)") // Description Swagger
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long id, // ID commande
            @AuthenticationPrincipal CustomUserDetails userDetails) { // Client authentifié
        return ResponseEntity.ok(orderService.cancelOrder(id, userDetails.getUser())); // Annule la commande
    }

    /**
     * Récupère toutes les commandes du système. Réservé aux administrateurs.
     */
    @GetMapping // Requête GET sur /api/orders
    @PreAuthorize("hasAnyAuthority('ADMIN')") // Sécurité : ADMIN uniquement
    @Operation(summary = "Toutes les commandes (ADMIN)") // Description Swagger
    public ResponseEntity<List<OrderResponse>> getAllOrders() { // Liste globale
        return ResponseEntity.ok(orderService.getAllOrders()); // Retourne tout
    }

    /**
     * Récupère les commandes contenant des produits du vendeur connecté.
     */
    @GetMapping("/seller") // Requête GET sur /api/orders/seller
    @PreAuthorize("hasAnyAuthority('SELLER','ADMIN')") // Sécurité : SELLER ou ADMIN
    @Operation(summary = "Commandes du vendeur connecté") // Description Swagger
    public ResponseEntity<List<OrderResponse>> getSellerOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(orderService.getSellerOrders(userDetails.getUser())); // Retourne les commandes du vendeur
    }

    /**
     * Confirme une commande. Accessible aux vendeurs et administrateurs.
     */
    @PutMapping("/{id}/confirm") // Requête PUT sur /api/orders/{id}/confirm
    @PreAuthorize("hasAnyAuthority('SELLER','ADMIN')") // Sécurité : SELLER ou ADMIN
    @Operation(summary = "Confirmer une commande (SELLER/ADMIN)") // Description Swagger
    public ResponseEntity<OrderResponse> confirmOrder(
            @PathVariable Long id, // ID commande
            @AuthenticationPrincipal CustomUserDetails userDetails) { // Utilisateur authentifié
        return ResponseEntity.ok(orderService.confirmOrder(id, userDetails.getUser())); // Confirme la commande
    }
}
