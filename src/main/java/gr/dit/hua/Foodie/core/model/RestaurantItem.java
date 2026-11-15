package gr.dit.hua.Foodie.core.model;


import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity
@Table(name = "foodie_restaurant_items")
public class RestaurantItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "item_name", nullable = false, unique = true, length = 255)
    private String name;

    @Column(name = "description", nullable = false, unique = true, length = 255)
    private String description;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "available", nullable = false)
    private boolean available;

    @OneToOne // One item belongs only to one restaurant
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurantBelongsTo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public RestaurantItem() {
    }

    public RestaurantItem(String name, String description, double price, boolean available, Restaurant restaurantBelongsTo) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
        this.restaurantBelongsTo = restaurantBelongsTo;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Restaurant getRestaurantBelongsTo() { return restaurantBelongsTo; }
    public void setRestaurantBelongsTo(Restaurant restaurantBelongsTo) { this.restaurantBelongsTo = restaurantBelongsTo; }

    public Instant getCreatedAt() { return createdAt; }
}
