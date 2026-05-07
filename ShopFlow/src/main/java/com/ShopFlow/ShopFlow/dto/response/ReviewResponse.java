package com.ShopFlow.ShopFlow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private String userName;
    private String customerName;
    private String productName;
    private Integer note;
    private String comment;
    private boolean approved;
    private LocalDateTime createdAt;
}
