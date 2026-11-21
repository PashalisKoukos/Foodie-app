package gr.dit.hua.Foodie.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "foodie_restaurant_items")
public class RestaurantItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Αφαίρεσα το unique = true για να μπορούν διαφορετικά εστιατόρια να έχουν π.χ. "Burger"
    @Column(name = "item_name", nullable = false, length = 255)
    @NotBlank(message = "A restaurant item must have a name")
    private String name;

    @Column(name = "description", nullable = false, length = 255)
    @NotBlank(message = "Restaurant item must have a description")
    private String description;

    @Column(name = "price", nullable = false)
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    @Column(name = "available", nullable = false)
    private boolean available;

    // ΔΙΟΡΘΩΣΗ: Από @OneToOne σε @ManyToOne (Πολυάριθμα items ανήκουν σε 1 εστιατόριο)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurantBelongsTo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public RestaurantItem() {
    }

    public RestaurantItem(String name, String description, BigDecimal price, boolean available, Restaurant restaurantBelongsTo) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
        this.restaurantBelongsTo = restaurantBelongsTo;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Restaurant getRestaurantBelongsTo() { return restaurantBelongsTo; }
    public void setRestaurantBelongsTo(Restaurant restaurantBelongsTo) { this.restaurantBelongsTo = restaurantBelongsTo; }

    public Instant getCreatedAt() { return createdAt; }
}