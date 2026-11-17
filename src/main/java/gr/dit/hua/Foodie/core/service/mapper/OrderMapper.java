package gr.dit.hua.Foodie.core.service.mapper;

import gr.dit.hua.Foodie.core.model.Order;
import gr.dit.hua.Foodie.core.model.RestaurantItem;
import gr.dit.hua.Foodie.core.service.model.OrderView;
import gr.dit.hua.Foodie.core.service.model.RestaurantItemView;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Component responsible for mapping {@link Order} entities to {@link OrderView} representation models.
 */
@Component
public class OrderMapper {

    private final RestaurantItemMapper itemMapper;


    public OrderMapper(RestaurantItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    /**
     * Converts an {@link Order} entity to its corresponding {@link OrderView} representation.
     * Handles null safety for the customer, restaurant, and items list associated with the order.
     *
     * @param order the order entity to convert
     * @return the corresponding {@link OrderView}, or null if the input order is null
     */
    public OrderView convertOrderToOrderView(Order order) {
        if (order == null) {
            return null;
        }

        String customerUsername = (order.getCustomer() != null) ? order.getCustomer().getUsername() : null;
        String restaurantName = (order.getRestaurant() != null) ? order.getRestaurant().getName() : null;

        // convert list of entities to a list of views
        List<RestaurantItemView> itemViews = new ArrayList<>();
        if (order.getItems() != null) {
            for (RestaurantItem item : order.getItems()) {
                itemViews.add(itemMapper.convertRestaurantItemToRestaurantItemView(item));
            }
        }

        return new OrderView(
                order.getId(),
                customerUsername,
                restaurantName,
                itemViews,
                order.getTotalPrice(),
                order.getStatus(),
                order.getDeliveryAddress(),
                order.getOrderType(),
                order.getCreatedAt()
        );
    }
}