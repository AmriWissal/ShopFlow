package com.ShopFlow.ShopFlow.mapper;

import com.ShopFlow.ShopFlow.dto.request.ReviewRequest;
import com.ShopFlow.ShopFlow.dto.response.ReviewResponse;
import com.ShopFlow.ShopFlow.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    /**
     * Convertit une requête de revue en entité Review.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "approved", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "customer", ignore = true)
    Review toEntity(ReviewRequest request);

    /**
     * Convertit une entité Review en ReviewResponse DTO.
     * Mappe le nom du client et du produit de manière sécurisée.
     */
    @Mapping(target = "userName", expression = "java(getCustomerName(review))")
    @Mapping(target = "customerName", expression = "java(getCustomerName(review))")
    @Mapping(target = "productName", expression = "java(getProductName(review))")
    ReviewResponse toResponse(Review review);

    /**
     * Méthode helper pour obtenir le nom du client de manière sécurisée.
     */
    default String getCustomerName(Review review) {
        if (review == null || review.getCustomer() == null) {
            return "Anonyme";
        }
        try {
            return review.getCustomer().getFullName();
        } catch (Exception e) {
            return "Client";
        }
    }

    /**
     * Méthode helper pour obtenir le nom du produit de manière sécurisée.
     */
    default String getProductName(Review review) {
        if (review == null || review.getProduct() == null) {
            return "Produit";
        }
        try {
            return review.getProduct().getName();
        } catch (Exception e) {
            return "Produit";
        }
    }
}
