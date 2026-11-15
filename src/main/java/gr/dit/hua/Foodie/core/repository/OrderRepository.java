package gr.dit.hua.Foodie.core.repository;

import gr.dit.hua.Foodie.core.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // find order by customer
    List<Order> findByCustomerId(Long customerId);
    // find order by restaurant
    List<Order> findByRestaurantId(Long restaurantId);
}
