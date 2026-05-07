package com.ShopFlow.ShopFlow.dto.request;

import com.ShopFlow.ShopFlow.entity.enums.CouponType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponRequest {
    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "Type is required")
    private CouponType type;

    @NotNull(message = "Value is required")
    private Double value;

    private LocalDateTime expirationDate;

    private Integer usageLimit;
}
