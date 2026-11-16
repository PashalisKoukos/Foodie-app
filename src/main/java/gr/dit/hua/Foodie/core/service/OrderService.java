package gr.dit.hua.Foodie.core.service;

import gr.dit.hua.Foodie.core.model.OrderStatus;
import gr.dit.hua.Foodie.core.model.OrderType;
import gr.dit.hua.Foodie.core.service.model.OrderView;
import java.util.List;

public interface OrderService {
    OrderView createOrder(Long customerId, Long restaurantId, List<Long> itemIds, OrderType orderType, String deliveryAddress);
    List<OrderView> getOrdersByCustomerId(Long customerId);
    List<OrderView> getOrdersByRestaurantId(Long restaurantId);
    OrderView updateOrderStatus(int orderId, OrderStatus newStatus);
}