package gr.dit.hua.Foodie.core.service.mapper;

import gr.dit.hua.Foodie.core.model.RestaurantItem;
import gr.dit.hua.Foodie.core.service.model.RestaurantItemView;
import org.springframework.stereotype.Component;

/**
 * Component responsible for mapping {@link RestaurantItem} entities to {@link RestaurantItemView} representation models.
 */
@Component
public class RestaurantItemMapper {

    /**
     * Converts a {@link RestaurantItem} entity to its corresponding {@link RestaurantItemView} representation.
     * Safely resolves the associated restaurant ID if the item belongs to a restaurant.
     *
     * @param item the restaurant item entity to convert
     * @return the corresponding {@link RestaurantItemView}, or null if the input item is null
     */
    public RestaurantItemView convertRestaurantItemToRestaurantItemView(RestaurantItem item) {
        if (item == null) return null;

        Long restaurantId;
        if(item.getRestaurantBelongsTo() != null){
            restaurantId = item.getRestaurantBelongsTo().getId();
        } else {
            restaurantId = null;
        }

        return new RestaurantItemView(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.isAvailable(),
                restaurantId
        );
    }
}