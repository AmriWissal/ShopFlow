package com.ShopFlow.ShopFlow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour représenter une variante de produit (ex: taille, couleur).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VariantResponse {
    private Long id;
    private String name; // Nom de la variante (ex: "Rouge", "XL")
    private Double priceDelta; // Différence de prix par rapport au prix de base
    private Integer stock; // Stock spécifique à cette variante
}
