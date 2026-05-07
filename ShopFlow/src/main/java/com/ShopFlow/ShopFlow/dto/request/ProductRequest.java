package com.ShopFlow.ShopFlow.dto.request;

import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

    private String name;
    private String description;
    private Double price;
    private Double promoPrice;
    private Integer stock;
    private Set<Long> categoryIds;
    private List<String> images;
    private Long sellerId; // ID du vendeur (optionnel, utilisé par l'admin)
}