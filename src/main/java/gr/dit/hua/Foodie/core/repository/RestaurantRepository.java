package gr.dit.hua.Foodie.core.repository;

import gr.dit.hua.Foodie.core.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    // find restaurants by region
    List<Restaurant> findByRegionIgnoreCase(String region);
    // find by name
    Optional<Restaurant> findByName(String name);
    // find by type of cuisine
    List<Restaurant> findByCuisineIgnoreCase(String cuisine);
    // find by the owner
    Optional<Restaurant> findByOwnerId(Long ownerId);
    // check the existence
    boolean existsByName(String name);
}
