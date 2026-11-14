package gr.dit.hua.Foodie.core.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "foodie_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ManyToOne // One customer can exist in multiple orders
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne // One restaraunt can exist in multiple orders
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @OneToMany // One order can have multiple items
    private List<RestaurantItem> items;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Order() {
    }

    public Order(User customer, Restaurant restaurant, List<RestaurantItem> items, BigDecimal totalPrice) {
        this.customer = customer;
        this.restaurant = restaurant;
        this.items = items;
        this.totalPrice = totalPrice;
    }

    public int getId() { return id; }

    public User getCustomer() { return customer; }
    public void setCustomer(User customer) { this.customer = customer; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public List<RestaurantItem> getItems() { return items; }
    public void setItems(List<RestaurantItem> items) { this.items = items; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public Instant getCreatedAt() { return createdAt; }
}
