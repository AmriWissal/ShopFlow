package com.ShopFlow.ShopFlow.service;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service fournissant des données spécifiques au marché tunisien.
 */
@Service
public class TunisiaDataService {

    /**
     * Liste des principales villes tunisiennes pour les adresses de livraison.
     */
    public static final List<String> TUNISIAN_CITIES = List.of(
            "Tunis",
            "Sfax",
            "Sousse",
            "Kairouan",
            "Bizerte",
            "Gabès",
            "Ariana",
            "Gafsa",
            "Monastir",
            "Ben Arous",
            "Kasserine",
            "Médenine",
            "Nabeul",
            "Tataouine",
            "Béja",
            "Jendouba",
            "Mahdia",
            "Siliana",
            "Kébili",
            "Zaghouan",
            "Manouba",
            "Tozeur",
            "Sidi Bouzid",
            "La Marsa",
            "Hammam-Lif",
            "Hammamet",
            "Djerba",
            "Kef",
            "Korba",
            "Menzel Bourguiba"
    );

    /**
     * Frais de livraison par défaut en Tunisie (en DT).
     */
    public static final double DEFAULT_SHIPPING_FEES = 7.0;

    /**
     * Frais de livraison gratuits à partir de (en DT).
     */
    public static final double FREE_SHIPPING_THRESHOLD = 100.0;

    /**
     * Calcule les frais de livraison en fonction du montant de la commande.
     */
    public double calculateShippingFees(double orderAmount) {
        if (orderAmount >= FREE_SHIPPING_THRESHOLD) {
            return 0.0;
        }
        return DEFAULT_SHIPPING_FEES;
    }

    /**
     * Formate un montant en dinars tunisiens.
     */
    public String formatPrice(double amount) {
        return String.format("%.3f DT", amount);
    }

    /**
     * Retourne la liste des villes tunisiennes.
     */
    public List<String> getTunisianCities() {
        return TUNISIAN_CITIES;
    }
}
