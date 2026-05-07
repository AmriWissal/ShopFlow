package com.ShopFlow.ShopFlow.dto.response;

import com.ShopFlow.ShopFlow.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Double subTotal;
    private Double discountAmount;
    private Double shippingFees;
    private Double totalPrice;
    private OrderStatus status;
    private String shippingAddress;
    private LocalDateTime createdAt;
    private String couponCode;
    private UserResponse user;
    private List<OrderItemResponse> items;
}
