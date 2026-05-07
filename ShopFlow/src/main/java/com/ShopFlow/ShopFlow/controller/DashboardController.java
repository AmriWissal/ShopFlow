package com.ShopFlow.ShopFlow.controller;

import com.ShopFlow.ShopFlow.security.CustomUserDetails;
import com.ShopFlow.ShopFlow.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController // Contrôleur pour le dashboard
@RequestMapping("/api/dashboard") // Route /api/dashboard
@RequiredArgsConstructor // Injection
@Tag(name = "Dashboard", description = "Endpoints pour les statistiques Admin et Seller") // Doc Swagger
public class DashboardController {

    private final DashboardService dashboardService; // Service dashboard

    /**
     * Récupère les statistiques globales (Réservé aux Admins).
     */
    @GetMapping("/admin") // Requête GET sur /api/dashboard/admin
    @PreAuthorize("hasAnyAuthority('ADMIN','ROLE_ADMIN')") // Sécurité : ADMIN uniquement
    @Operation(summary = "Statistiques globales (ADMIN)") // Description Swagger
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        return ResponseEntity.ok(dashboardService.getAdminStats()); // Retourne les stats globales
    }

    /**
     * Récupère les statistiques propres au vendeur connecté.
     */
    @GetMapping("/seller") // Requête GET sur /api/dashboard/seller
    @PreAuthorize("hasAnyAuthority('SELLER','ROLE_SELLER')") // Sécurité : SELLER uniquement
    @Operation(summary = "Statistiques du vendeur connecté") // Description Swagger
    public ResponseEntity<Map<String, Object>> getSellerStats(@AuthenticationPrincipal CustomUserDetails userDetails) { // Vendeur connecté
        return ResponseEntity.ok(dashboardService.getSellerStats(userDetails.getUser())); // Retourne les stats du vendeur
    }
}
