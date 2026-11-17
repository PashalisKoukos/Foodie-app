package gr.dit.hua.Foodie.core.service.mapper;

import gr.dit.hua.Foodie.core.model.Restaurant;
import gr.dit.hua.Foodie.core.service.model.RestaurantView;
import org.springframework.stereotype.Component;

/**
 * Component responsible for mapping {@link Restaurant} entities to {@link RestaurantView} representation models.
 */
@Component
public class RestaurantMapper {

    /**
     * Converts a {@link Restaurant} entity to its corresponding {@link RestaurantView} representation.
     * Safely resolves the associated owner's username if an owner is assigned to the restaurant.
     *
     * @param restaurant the restaurant entity to convert
     * @return the corresponding {@link RestaurantView}, or null if the input restaurant is null
     */
    public RestaurantView convertRestaurantToRestaurantView(Restaurant restaurant) {
        if (restaurant == null) {
            return null;
        }

        String ownerUsername = (restaurant.getOwner() != null) ? restaurant.getOwner().getUsername() : null;

        return new RestaurantView(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCuisine(),
                restaurant.getRegion(),
                restaurant.getAddress(),
                restaurant.isOpen(),
                restaurant.getMinimumOrderAmount(),
                ownerUsername
        );
    }
}