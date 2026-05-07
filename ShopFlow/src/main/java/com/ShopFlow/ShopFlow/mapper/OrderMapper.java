package com.ShopFlow.ShopFlow.mapper;

import com.ShopFlow.ShopFlow.dto.response.OrderItemResponse;
import com.ShopFlow.ShopFlow.dto.response.OrderResponse;
import com.ShopFlow.ShopFlow.entity.Order;
import com.ShopFlow.ShopFlow.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface OrderMapper {

    /**
     * Convertit une entité Order en OrderResponse DTO.
     * Mappe explicitement les champs dont les noms diffèrent.
     */
    @Mapping(target = "items", source = "orderItems")
    @Mapping(target = "totalPrice", source = "totalTTC")
    @Mapping(target = "createdAt", source = "orderDate")
    @Mapping(target = "user", source = "customer")
    @Mapping(target = "couponCode", expression = "java(order.getAppliedCoupon() != null ? order.getAppliedCoupon().getCode() : null)")
    OrderResponse toResponse(Order order);

    /**
     * Convertit un OrderItem en OrderItemResponse DTO.
     */
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImage", expression = "java(getProductImage(orderItem))")
    OrderItemResponse toResponse(OrderItem orderItem);

    /**
     * Récupère la première image du produit.
     */
    default String getProductImage(OrderItem orderItem) {
        if (orderItem.getProduct() == null || orderItem.getProduct().getImages() == null || orderItem.getProduct().getImages().isEmpty()) {
            return null;
        }
        return orderItem.getProduct().getImages().get(0);
    }
}
