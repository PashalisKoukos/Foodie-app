package gr.dit.hua.Foodie.core.service;

import gr.dit.hua.Foodie.core.service.model.RestaurantItemView;
import java.util.List;

public interface RestaurantItemService {
    List<RestaurantItemView> getItemsByRestaurantId(Long restaurantId);
}