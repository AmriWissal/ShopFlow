package com.ShopFlow.ShopFlow.mapper;

import com.ShopFlow.ShopFlow.dto.request.ProductRequest;
import com.ShopFlow.ShopFlow.dto.response.ProductResponse;
import com.ShopFlow.ShopFlow.entity.Product;
import com.ShopFlow.ShopFlow.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {CategoryMapper.class, VariantMapper.class, ReviewMapper.class, UserMapper.class})
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "seller", ignore = true)
    @Mapping(target = "variants", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    Product toEntity(ProductRequest request);

    /**
     * Mapping avec gestion sécurisée des reviews lazy.
     */
    @Mapping(target = "categories", source = "categories")
    @Mapping(target = "averageNote", expression = "java(calculateAverageNote(product))")
    @Mapping(target = "reviews", expression = "java(null)") // Ignorer les reviews pour éviter lazy loading
    ProductResponse toResponse(Product product);

    default Double calculateAverageNote(Product product) {
        try {
            if (product.getReviews() == null || product.getReviews().isEmpty()) {
                return 0.0;
            }
            return product.getReviews().stream()
                    .filter(Review::isApproved)
                    .mapToInt(Review::getNote)
                    .average()
                    .orElse(0.0);
        } catch (Exception e) {
            // Si les reviews ne sont pas chargées (lazy), retourner 0.0
            return 0.0;
        }
    }
}