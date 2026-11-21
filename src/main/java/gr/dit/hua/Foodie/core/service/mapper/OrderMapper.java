package gr.dit.hua.Foodie.core.service.mapper;

import gr.dit.hua.Foodie.core.model.Order;
import gr.dit.hua.Foodie.core.model.OrderItem;
import gr.dit.hua.Foodie.core.service.model.OrderItemView;
import gr.dit.hua.Foodie.core.service.model.OrderView;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Component responsible for mapping {@link Order} entities to {@link OrderView} representation models.
 * It must be called inside a service transaction, because the order's items are loaded lazily.
 */
@Component
public class OrderMapper {

    /**
     * Converts an {@link Order} entity to its corresponding {@link OrderView}.
     *
     * @param order the order entity to convert
     * @return the corresponding {@link OrderView}, or null if the input order is null
     */
    public OrderView convertOrderToOrderView(Order order) {
        if (order == null) {
            return null;
        }

        // customer and restaurant are mandatory in the database (nullable = false),
        // so no null checks are needed for them
        List<OrderItemView> itemViews = new ArrayList<>();
        for (OrderItem line : order.getItems()) {
            itemViews.add(convertOrderItemToOrderItemView(line));
        }

        return new OrderView(
                (long) order.getId(),
                order.getCustomer().getUsername(),
                order.getRestaurant().getName(),
                itemViews,
                order.getTotalPrice(),
                order.getStatus(),
                order.getDeliveryAddress(),
                order.getOrderType(),
                order.getCreatedAt()
        );
    }

    // One order line: the name comes from the menu item, while the price is the one
    // saved when the order was placed, so later price changes do not affect it
    private OrderItemView convertOrderItemToOrderItemView(OrderItem line) {
        BigDecimal lineTotal = line.getUnitPrice()
                .multiply(BigDecimal.valueOf(line.getQuantity()));
        return new OrderItemView(
                line.getId(),
                line.getItem().getName(),
                line.getQuantity(),
                line.getUnitPrice(),
                lineTotal
        );
    }
}