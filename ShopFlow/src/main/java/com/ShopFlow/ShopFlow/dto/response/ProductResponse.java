package com.ShopFlow.ShopFlow.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Double promoPrice;
    private Integer stock;
    private boolean active;
    private LocalDateTime createdAt;

    private Set<CategoryResponse> categories;
    private List<String> images;
    private List<VariantResponse> variants; // Liste des variantes disponibles
    private List<ReviewResponse> reviews; // Liste des avis clients
    private UserResponse seller; // Vendeur du produit

    @Builder.Default
    private Double averageNote = 0.0;
}