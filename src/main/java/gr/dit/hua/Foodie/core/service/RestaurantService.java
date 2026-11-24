package gr.dit.hua.Foodie.core.service;

import gr.dit.hua.Foodie.core.service.model.RestaurantView;
import java.util.List;
import java.util.Optional;

public interface RestaurantService {
    List<RestaurantView> getAllRestaurants();
    List<RestaurantView> getRestaurantsByRegion(String region);
    List<RestaurantView> getRestaurantsByCuisine(String cuisine);
    Optional<RestaurantView> getRestaurantByOwnerId(Long ownerId);
    Optional<RestaurantView> getRestaurantById(Long restaurantId);
}