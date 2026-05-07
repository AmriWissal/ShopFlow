package com.ShopFlow.ShopFlow.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequest {
    @NotNull(message = "Product ID is required")
    private Long productId;

    @Min(1) @Max(5)
    private Integer note;

    @NotBlank(message = "Comment is required")
    private String comment;
}
