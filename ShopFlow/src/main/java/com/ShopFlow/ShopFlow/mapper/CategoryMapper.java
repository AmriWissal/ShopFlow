package com.ShopFlow.ShopFlow.mapper;

import com.ShopFlow.ShopFlow.dto.response.CategoryResponse;
import com.ShopFlow.ShopFlow.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring") // Déclare l'interface comme un mapper MapStruct géré par Spring
public interface CategoryMapper {
    /**
     * Convertit une catégorie simple en réponse DTO.
     */
    @Mapping(target = "editable", ignore = true)
    CategoryResponse toResponse(Category category); // Mapping standard champ à champ

    /**
     * Convertit une liste de catégories racines en arbre de DTOs.
     * La récursion est gérée automatiquement car CategoryResponse contient une liste de CategoryResponse.
     */
    List<CategoryResponse> toResponseTree(List<Category> categories); // Mapping de collection
}