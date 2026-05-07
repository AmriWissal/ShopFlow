package com.ShopFlow.ShopFlow.controller;

import com.ShopFlow.ShopFlow.dto.request.CouponRequest;
import com.ShopFlow.ShopFlow.dto.response.CouponResponse;
import com.ShopFlow.ShopFlow.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Contrôleur pour les coupons
@RequestMapping("/api/coupons") // Route /api/coupons
@RequiredArgsConstructor // Injection
@Tag(name = "Coupon", description = "Endpoints pour la gestion et la validation des coupons") // Doc Swagger
public class CouponController {

    private final CouponService couponService; // Service coupons

    /**
     * Liste tous les coupons (ADMIN uniquement).
     */
    @GetMapping // GET sur /api/coupons
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Lister tous les coupons (ADMIN)")
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    /**
     * Récupère un coupon par son ID (ADMIN uniquement).
     */
    @GetMapping("/{id}") // GET sur /api/coupons/{id}
    @PreAuthorize("hasAuthority('ADMIN')") // Sécurité : ADMIN
    @Operation(summary = "Récupérer un coupon par ID (ADMIN)") // Description Swagger
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.getCouponById(id));
    }

    /**
     * Crée un nouveau coupon (ADMIN uniquement).
     */
    @PostMapping // POST sur /api/coupons
    @PreAuthorize("hasAuthority('ADMIN')") // Sécurité : ADMIN
    @Operation(summary = "Créer un coupon (ADMIN)") // Description Swagger
    public ResponseEntity<CouponResponse> createCoupon(@Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(couponService.createCoupon(request)); // Appelle création
    }

    /**
     * Met à jour un coupon existant (ADMIN uniquement).
     */
    @PutMapping("/{id}") // PUT sur /api/coupons/{id}
    @PreAuthorize("hasAuthority('ADMIN')") // Sécurité : ADMIN
    @Operation(summary = "Modifier un coupon (ADMIN)") // Description Swagger
    public ResponseEntity<CouponResponse> updateCoupon(@PathVariable Long id, @Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(couponService.updateCoupon(id, request)); // Appelle modification
    }

    /**
     * Active ou désactive un coupon (ADMIN uniquement).
     */
    @PatchMapping("/{id}/toggle-status") // PATCH sur /api/coupons/{id}/toggle-status
    @PreAuthorize("hasAuthority('ADMIN')") // Sécurité : ADMIN
    @Operation(summary = "Activer/Désactiver un coupon (ADMIN)") // Description Swagger
    public ResponseEntity<CouponResponse> toggleCouponStatus(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.toggleCouponStatus(id));
    }

    /**
     * Active un coupon (ADMIN uniquement).
     */
    @PatchMapping("/{id}/activate") // PATCH sur /api/coupons/{id}/activate
    @PreAuthorize("hasAuthority('ADMIN')") // Sécurité : ADMIN
    @Operation(summary = "Activer un coupon (ADMIN)") // Description Swagger
    public ResponseEntity<CouponResponse> activateCoupon(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.activateCoupon(id));
    }

    /**
     * Désactive un coupon (ADMIN uniquement).
     */
    @PatchMapping("/{id}/deactivate") // PATCH sur /api/coupons/{id}/deactivate
    @PreAuthorize("hasAuthority('ADMIN')") // Sécurité : ADMIN
    @Operation(summary = "Désactiver un coupon (ADMIN)") // Description Swagger
    public ResponseEntity<CouponResponse> deactivateCoupon(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.deactivateCoupon(id));
    }

    /**
     * Supprime un coupon (ADMIN uniquement).
     */
    @DeleteMapping("/{id}") // DELETE sur /api/coupons/{id}
    @PreAuthorize("hasAuthority('ADMIN')") // Sécurité : ADMIN
    @Operation(summary = "Supprimer un coupon (ADMIN)") // Description Swagger
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id); // Appelle suppression
        return ResponseEntity.noContent().build(); // Retourne 204
    }

    /**
     * Vérifie la validité d'un code promo.
     */
    @GetMapping("/validate/{code}") // GET sur /api/coupons/validate/{code}
    @Operation(summary = "Vérifier la validité d'un code") // Description Swagger
    public ResponseEntity<CouponResponse> validateCoupon(@PathVariable String code) {
        return ResponseEntity.ok(couponService.getCouponByCode(code)); // Retourne les infos si valide
    }
}
