package com.ShopFlow.ShopFlow.mapper;

import com.ShopFlow.ShopFlow.dto.response.FavoriteResponse;
import com.ShopFlow.ShopFlow.entity.Favorite;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FavoriteMapper {

    private final ProductMapper productMapper;

    /**
     * Convertit une entité Favorite en DTO FavoriteResponse
     */
    public FavoriteResponse toResponse(Favorite favorite) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .product(productMapper.toResponse(favorite.getProduct()))
                .createdAt(favorite.getCreatedAt())
                .build();
    }
}
