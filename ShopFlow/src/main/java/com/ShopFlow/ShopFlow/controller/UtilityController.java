package com.ShopFlow.ShopFlow.controller;

import com.ShopFlow.ShopFlow.service.TunisiaDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour les utilitaires et données de référence.
 */
@RestController
@RequestMapping("/api/utilities")
@RequiredArgsConstructor
@Tag(name = "Utilities", description = "Endpoints pour les données de référence (villes, frais de port, etc.)")
public class UtilityController {

    private final TunisiaDataService tunisiaDataService;

    /**
     * Récupère la liste des villes tunisiennes pour les formulaires d'adresse.
     */
    @GetMapping("/cities")
    @Operation(summary = "Liste des villes tunisiennes")
    public ResponseEntity<List<String>> getTunisianCities() {
        return ResponseEntity.ok(tunisiaDataService.getTunisianCities());
    }

    /**
     * Calcule les frais de livraison en fonction du montant de la commande.
     */
    @GetMapping("/shipping-fees")
    @Operation(summary = "Calculer les frais de livraison")
    public ResponseEntity<Map<String, Object>> calculateShippingFees(@RequestParam double amount) {
        double fees = tunisiaDataService.calculateShippingFees(amount);
        return ResponseEntity.ok(Map.of(
                "amount", amount,
                "shippingFees", fees,
                "freeShippingThreshold", TunisiaDataService.FREE_SHIPPING_THRESHOLD,
                "formattedFees", tunisiaDataService.formatPrice(fees)
        ));
    }

    /**
     * Retourne les informations de configuration pour le marché tunisien.
     */
    @GetMapping("/config")
    @Operation(summary = "Configuration du marché tunisien")
    public ResponseEntity<Map<String, Object>> getMarketConfig() {
        return ResponseEntity.ok(Map.of(
                "currency", "DT",
                "currencySymbol", "DT",
                "defaultShippingFees", TunisiaDataService.DEFAULT_SHIPPING_FEES,
                "freeShippingThreshold", TunisiaDataService.FREE_SHIPPING_THRESHOLD,
                "country", "Tunisie",
                "locale", "fr-TN"
        ));
    }
}
