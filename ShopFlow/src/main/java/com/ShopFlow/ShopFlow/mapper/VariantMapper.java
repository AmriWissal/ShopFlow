package com.ShopFlow.ShopFlow.mapper;

import com.ShopFlow.ShopFlow.dto.response.VariantResponse;
import com.ShopFlow.ShopFlow.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VariantMapper {

    /**
     * Convertit une variante de produit en DTO.
     * Mappe le nom à partir de l'attribut et de la valeur (ex: "Taille: XL").
     */
    @Mapping(target = "name", expression = "java(variant.getAttribute() + \": \" + variant.getValue())")
    @Mapping(target = "stock", source = "extraStock")
    VariantResponse toResponse(ProductVariant variant);
}
