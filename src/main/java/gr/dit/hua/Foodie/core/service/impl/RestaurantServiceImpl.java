package gr.dit.hua.Foodie.core.service.impl;
import gr.dit.hua.Foodie.core.model.Restaurant;
import gr.dit.hua.Foodie.core.repository.RestaurantRepository;
import gr.dit.hua.Foodie.core.service.RestaurantService;
import gr.dit.hua.Foodie.core.service.mapper.RestaurantMapper;
import gr.dit.hua.Foodie.core.service.model.RestaurantView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link RestaurantService} interface.
 * Handles the business logic for managing and retrieving restaurant data,
 * and mapping entities to their corresponding view models.
 */
@Service
@Transactional(readOnly = true)
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;

    public RestaurantServiceImpl(RestaurantRepository restaurantRepository, RestaurantMapper restaurantMapper) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantMapper = restaurantMapper;
    }

    /**
     * Retrieves all restaurants from the database and converts them into view objects.
     *
     * @return a list of {@link RestaurantView} representing all available restaurants
     */
    @Override
    public List<RestaurantView> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        List<RestaurantView> views = new ArrayList<>();

        for (Restaurant restaurant : restaurants) {
            views.add(restaurantMapper.convertRestaurantToRestaurantView(restaurant));
        }

        return views;
    }

    /**
     * Retrieves restaurants filtered by the specified region, ignoring case,
     * and converts them into view objects.
     *
     * @param region the region to filter restaurants by
     * @return a list of {@link RestaurantView} located in the specified region
     */
    @Override
    public List<RestaurantView> getRestaurantsByRegion(String region) {
        List<Restaurant> restaurants = restaurantRepository.findByRegionIgnoreCase(region);
        List<RestaurantView> views = new ArrayList<>();

        for (Restaurant restaurant : restaurants) {
            views.add(restaurantMapper.convertRestaurantToRestaurantView(restaurant));
        }

        return views;
    }

    /**
     * Retrieves restaurants filtered by the specified cuisine type, ignoring case,
     * and converts them into view objects.
     *
     * @param cuisine the cuisine type to filter restaurants by
     * @return a list of {@link RestaurantView} matching the specified cuisine
     */
    @Override
    public List<RestaurantView> getRestaurantsByCuisine(String cuisine) {
        List<Restaurant> restaurants = restaurantRepository.findByCuisineIgnoreCase(cuisine);
        List<RestaurantView> views = new ArrayList<>();

        for (Restaurant restaurant : restaurants) {
            views.add(restaurantMapper.convertRestaurantToRestaurantView(restaurant));
        }

        return views;
    }

    /**
     * Retrieves a restaurant associated with a specific owner ID and converts it into a view object.
     *
     * @param ownerId the ID of the restaurant owner
     * @return an {@link Optional} containing the {@link RestaurantView} if found, or empty Optional otherwise
     */
    @Override
    public Optional<RestaurantView> getRestaurantByOwnerId(Long ownerId) {
        Optional<Restaurant> restaurantOpt = restaurantRepository.findByOwnerId(ownerId);

        if (restaurantOpt.isPresent()) {
            RestaurantView view = restaurantMapper.convertRestaurantToRestaurantView(restaurantOpt.get());
            return Optional.of(view);
        }

        return Optional.empty();
    }

    @Override
    public Optional<RestaurantView> getRestaurantById(Long restaurantId) {
        Optional<Restaurant> restaurantOpt = restaurantRepository.findById(restaurantId);
        if (restaurantOpt.isPresent()) {
            RestaurantView view = restaurantMapper.convertRestaurantToRestaurantView(restaurantOpt.get());
            return Optional.of(view);
        }
        return Optional.empty();
    }
}