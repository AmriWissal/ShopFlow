package com.ShopFlow.ShopFlow.dto.response;

import com.ShopFlow.ShopFlow.entity.enums.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponResponse {
    private Long id;
    private String code;
    private CouponType type;
    private Double value;
    private LocalDateTime expirationDate;
    private Integer usageLimit;
    private Integer currentUsage;
    private Boolean active;
}
