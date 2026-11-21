package gr.dit.hua.Foodie.core.service.model;

import gr.dit.hua.Foodie.core.model.OrderStatus;
import gr.dit.hua.Foodie.core.model.OrderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderView(
        Long id,
        String customerUsername,
        String restaurantName,
        List<OrderItemView> items,
        BigDecimal totalPrice,
        OrderStatus status,
        String deliveryAddress,
        OrderType orderType,
        Instant createdAt
) {
}