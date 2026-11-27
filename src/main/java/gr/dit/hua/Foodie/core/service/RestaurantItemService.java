package gr.dit.hua.Foodie.core.service;

import gr.dit.hua.Foodie.core.service.model.RestaurantItemView;

import java.math.BigDecimal;
import java.util.List;

public interface RestaurantItemService {
    List<RestaurantItemView> getItemsByRestaurantId(Long restaurantId);
    void addItem(Long restaurantId, String ownerUsername, String name, String description, BigDecimal price, boolean available);
    void updatePrice(Long itemId, String ownerUsername, BigDecimal price);
    void deleteItem(Long itemId, String ownerUsername);
    void setAvailability(Long itemId, String ownerUsername, boolean available);
}