package gr.dit.hua.Foodie.core.service.model;

import gr.dit.hua.Foodie.core.model.OrderStatus;
import gr.dit.hua.Foodie.core.model.OrderType;
import java.time.Instant;
import java.util.List;

public record OrderView(
        int id,
        String customerUsername,
        String restaurantName,
        List<RestaurantItemView> items,
        double totalPrice,
        OrderStatus status,
        String deliveryAddress,
        OrderType orderType,
        Instant createdAt
) {
}