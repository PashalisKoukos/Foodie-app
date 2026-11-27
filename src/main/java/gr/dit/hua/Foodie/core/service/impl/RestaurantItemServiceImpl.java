package gr.dit.hua.Foodie.core.service.impl;

import gr.dit.hua.Foodie.core.model.Restaurant;
import gr.dit.hua.Foodie.core.model.RestaurantItem;
import gr.dit.hua.Foodie.core.repository.RestaurantItemRepository;
import gr.dit.hua.Foodie.core.repository.RestaurantRepository;
import gr.dit.hua.Foodie.core.service.RestaurantItemService;
import gr.dit.hua.Foodie.core.service.mapper.RestaurantItemMapper;
import gr.dit.hua.Foodie.core.service.model.RestaurantItemView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link RestaurantItemService} providing business logic for retrieving,
 * creating, updating, and deleting restaurant menu items while enforcing owner authorization.
 */
@Service
@Transactional(readOnly = true)
public class RestaurantItemServiceImpl implements RestaurantItemService {

    private final RestaurantItemRepository itemRepository;
    private final RestaurantItemMapper itemMapper;
    private final RestaurantRepository restaurantRepository;

    public RestaurantItemServiceImpl(RestaurantItemRepository itemRepository, RestaurantItemMapper itemMapper, RestaurantRepository restaurantRepository) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * method to get items by the restaurant id
     *
     * @param restaurantId to get the items by the restaurant
     * @return a list with RestaurantItems views
     */
    @Override
    public List<RestaurantItemView> getItemsByRestaurantId(Long restaurantId) {
        List<RestaurantItem> items = itemRepository.findByRestaurantBelongsToId(restaurantId);
        List<RestaurantItemView> views = new ArrayList<>();

        for (RestaurantItem item : items) {
            views.add(itemMapper.convertRestaurantItemToRestaurantItemView(item));
        }

        return views;
    }

    /*
     * Configured with read-only transactions by default for efficient data retrieval,
     * while mutating methods explicitly declare read-write {@code @Transactional}
     * boundaries to guarantee atomicity and rollback on failure.
     * ACID !!
     */

    /**
     * Validates input fields, verifies restaurant ownership, and persists a new menu item.
     *
     * @param restaurantId  the ID of the restaurant to which the item will belong
     * @param ownerUsername the username of the authenticated restaurant owner
     * @param name          the name of the menu item
     * @param description   the description of the menu item
     * @param price         the price of the menu item
     * @param available     whether the item is initially available
     * @throws IllegalArgumentException if any field is invalid or the restaurant does not exist
     * @throws SecurityException        if {@code ownerUsername} does not own the target restaurant
     */
    @Transactional
    @Override
    public void addItem(Long restaurantId, String ownerUsername, String name, String description, BigDecimal price, boolean available) {
        // check the name
        if(name == null || name.trim().isEmpty() || name.isBlank()) throw new IllegalArgumentException("Item name must not be blank.");
        // check price
        if(price == null || price.compareTo(BigDecimal.ZERO) == 0) throw new IllegalArgumentException("Price is not accepted.");
        // check description
        if(description == null || description.trim().isEmpty()) throw new IllegalArgumentException("Description must not be blank.");

        // All ok
        String cleanDescription = description.trim();
        String cleanName = name.trim();
        RestaurantItem item = new RestaurantItem(
                cleanName,
                cleanDescription,
                price,
                available,
                findRestaurant(restaurantId, ownerUsername)
        );

        itemRepository.save(item);
    }

    /**
     * Updates the price of an existing menu item after validating the price and verifying ownership.
     *
     * @param itemId        the ID of the menu item to update
     * @param ownerUsername the username of the authenticated restaurant owner
     * @param price         the new price for the menu item
     * @throws IllegalArgumentException if the price is invalid or the item does not exist
     * @throws SecurityException        if {@code ownerUsername} does not own the restaurant containing the item
     */
    @Transactional
    @Override
    public void updatePrice(Long itemId, String ownerUsername, BigDecimal price) {
        //check price
        if(price == null || price.compareTo(BigDecimal.ZERO) == 0) throw new IllegalArgumentException("Price is not accepted.");
        // ok
        RestaurantItem item = findItem(itemId, ownerUsername);
        item.setPrice(price);
        itemRepository.save(item);

    }

    /**
     * Deletes an existing menu item after verifying owner authorization.
     *
     * @param itemId        the ID of the menu item to delete
     * @param ownerUsername the username of the authenticated restaurant owner
     * @throws IllegalArgumentException if the menu item does not exist
     * @throws SecurityException        if {@code ownerUsername} does not own the restaurant containing the item
     */
    @Transactional
    @Override
    public void deleteItem(Long itemId, String ownerUsername) {
        itemRepository.delete(findItem(itemId, ownerUsername));
    }

    /**
     * Updates the availability status of an existing menu item after verifying owner authorization.
     *
     * @param itemId        the ID of the menu item to update
     * @param ownerUsername the username of the authenticated restaurant owner
     * @param available     the new availability status
     * @throws IllegalArgumentException if the menu item does not exist
     * @throws SecurityException        if {@code ownerUsername} does not own the restaurant containing the item
     */
    @Transactional
    @Override
    public void setAvailability(Long itemId, String ownerUsername, boolean available) {
        RestaurantItem item = findItem(itemId, ownerUsername);
        item.setAvailable(available);
        itemRepository.save(item);
    }

    /**
     * Finds a restaurant by ID and verifies that the specified user is its owner.
     *
     * @param restaurantId  the ID of the restaurant to look up
     * @param ownerUsername the username expected to own the restaurant
     * @return the matching {@link Restaurant} entity
     * @throws IllegalArgumentException if no restaurant is found with {@code restaurantId}
     * @throws SecurityException        if the restaurant has no owner or is not owned by {@code ownerUsername}
     */
    private Restaurant findRestaurant(Long restaurantId, String ownerUsername) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new IllegalArgumentException("Restaurant not found: " + restaurantId));
        if (restaurant.getOwner() == null || !ownerUsername.equals(restaurant.getOwner().getUsername())) {
            throw new SecurityException("You are not the owner of this restaurant.");
        }
        return restaurant;
    }

    /**
     * Finds a menu item by ID and verifies that the specified user owns the restaurant it belongs to.
     *
     * @param itemId        the ID of the menu item to look up
     * @param ownerUsername the username expected to own the associated restaurant
     * @return the matching {@link RestaurantItem} entity
     * @throws IllegalArgumentException if no menu item is found with {@code itemId}
     * @throws SecurityException        if the item lacks an associated restaurant/owner or is not owned by {@code ownerUsername}
     */
    private RestaurantItem findItem(Long itemId, String ownerUsername) {
        RestaurantItem item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + itemId));

        Restaurant restaurant = item.getRestaurantBelongsTo();
        if (restaurant == null || restaurant.getOwner() == null || !ownerUsername.equals(restaurant.getOwner().getUsername())) throw new SecurityException("You are not authorized to modify this menu item.");
        return item;
    }
}