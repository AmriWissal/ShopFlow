package com.ShopFlow.ShopFlow.controller;

import com.ShopFlow.ShopFlow.dto.request.ReviewRequest;
import com.ShopFlow.ShopFlow.dto.response.ReviewResponse;
import com.ShopFlow.ShopFlow.security.CustomUserDetails;
import com.ShopFlow.ShopFlow.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Review", description = "Endpoints pour la gestion des avis produits")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Laisser un avis sur un produit.
     */
    @PostMapping
    @Operation(summary = "Ajouter un avis")
    public ResponseEntity<?> addReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReviewRequest request) {
        try {
            ReviewResponse response = reviewService.addReview(userDetails.getUser(), request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Retourne un message d'erreur clair au lieu d'une erreur 500
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    // Classe interne pour les messages d'erreur
    private static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }

    /**
     * Voir tous les avis approuvés pour un produit donné.
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "Avis approuvés d'un produit")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getApprovedReviewsForProduct(productId));
    }

    /**
     * Voir tous les avis en attente de validation (Admin).
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN')")
    @Operation(summary = "Avis en attente (Admin)")
    public ResponseEntity<List<ReviewResponse>> getPendingReviews() {
        return ResponseEntity.ok(reviewService.getPendingReviews());
    }

    /**
     * Voir tous les avis (Admin).
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN')")
    @Operation(summary = "Tous les avis (Admin)")
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    /**
     * Approuver un avis. Réservé aux administrateurs.
     */
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN')")
    @Operation(summary = "Approuver un avis (Admin)")
    public ResponseEntity<ReviewResponse> approveReview(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.approveReview(id));
    }
}
