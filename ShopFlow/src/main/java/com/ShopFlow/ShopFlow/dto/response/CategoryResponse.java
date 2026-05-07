package com.ShopFlow.ShopFlow.dto.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;

    // 🌳 pour affichage tree
    private List<CategoryResponse> subCategories;

    // 🔐 pour Angular (option pro)
    private boolean editable;
}