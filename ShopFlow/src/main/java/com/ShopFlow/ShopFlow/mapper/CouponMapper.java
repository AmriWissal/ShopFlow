package com.ShopFlow.ShopFlow.mapper;

import com.ShopFlow.ShopFlow.dto.request.CouponRequest;
import com.ShopFlow.ShopFlow.dto.response.CouponResponse;
import com.ShopFlow.ShopFlow.entity.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    /**
     * Convertit une requête de coupon en entité Coupon.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentUsage", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "expiryDate", source = "expirationDate")
    @Mapping(target = "maxUsage", source = "usageLimit")
    Coupon toEntity(CouponRequest request);

    /**
     * Convertit une entité Coupon en CouponResponse DTO.
     */
    @Mapping(target = "expirationDate", source = "expiryDate")
    @Mapping(target = "usageLimit", source = "maxUsage")
    CouponResponse toResponse(Coupon coupon);
}
