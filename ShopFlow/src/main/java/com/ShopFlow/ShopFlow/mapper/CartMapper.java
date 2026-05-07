package com.ShopFlow.ShopFlow.mapper;

import com.ShopFlow.ShopFlow.dto.response.CartItemResponse;
import com.ShopFlow.ShopFlow.dto.response.CartResponse;
import com.ShopFlow.ShopFlow.entity.Cart;
import com.ShopFlow.ShopFlow.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    /**
     * Convertit un panier (Cart) en CartResponse DTO.
     */
    @Mapping(target = "items", source = "items")
    CartResponse toResponse(Cart cart);

    /**
     * Convertit un élément du panier (CartItem) en CartItemResponse DTO.
     */
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImage", expression = "java(getProductImage(cartItem))")
    @Mapping(target = "unitPrice", expression = "java(calculateUnitPrice(cartItem))")
    CartItemResponse toResponse(CartItem cartItem);

    /**
     * Récupère la première image du produit.
     */
    default String getProductImage(CartItem cartItem) {
        if (cartItem.getProduct() == null || cartItem.getProduct().getImages() == null || cartItem.getProduct().getImages().isEmpty()) {
            return null;
        }
        return cartItem.getProduct().getImages().get(0);
    }

    /**
     * Calcule le prix unitaire d'un produit en tenant compte du variant si présent.
     */
    default Double calculateUnitPrice(CartItem cartItem) {
        if (cartItem.getProduct() == null) return 0.0;
        Double price = cartItem.getProduct().getPrice();
        if (cartItem.getVariant() != null && cartItem.getVariant().getPriceDelta() != null) {
            price += cartItem.getVariant().getPriceDelta();
        }
        return price;
    }
}
