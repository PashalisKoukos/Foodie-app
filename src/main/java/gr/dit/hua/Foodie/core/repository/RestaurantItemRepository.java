package gr.dit.hua.Foodie.core.repository;

import gr.dit.hua.Foodie.core.model.RestaurantItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantItemRepository extends JpaRepository<RestaurantItem, Long> {
    // find all food items that belongs to a single restaurant
    List<RestaurantItem> findByRestaurantBelongsToId(Long restaurantId);
}
