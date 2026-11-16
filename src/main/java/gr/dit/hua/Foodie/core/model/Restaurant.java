package gr.dit.hua.Foodie.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "foodie_restaurant")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "restaurant_name", nullable = false, unique = true, length = 255)
    @NotBlank(message = "A restaurant must have a name")
    private String name;

    @Column(name = "cuisine_type", nullable = false, length = 50)
    @NotBlank(message = "Every restaurant has a type of cuisine")
    private String cuisine;

    @Column(name = "region", nullable = false, length = 50)
    @NotBlank(message = "Every restaurant has a region")
    private String region;

    @Column(name = "address", nullable = false, unique = true,length = 100)
    @NotBlank(message = "Every restaurant has an address")
    @Size(min = 1, max = 100)
    private String address;

    @Column(name = "isOpen", nullable = false)
    private boolean isOpen;

    @OneToOne // one owner to one restaurant
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @PositiveOrZero(message = "Minimum order amount cannot be negative")
    @Column(name = "minimum_order_amount", nullable = false)
    private double minimumOrderAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Restaurant() {
    }

    public Restaurant(String name, String cuisine, String region, String address, boolean isOpen,double minimumOrderAmount, User owner) {
        this.name = name;
        this.cuisine = cuisine;
        this.region = region;
        this.address = address;
        this.isOpen = isOpen;
        this.minimumOrderAmount = minimumOrderAmount;
        this.owner = owner;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public boolean isOpen() { return isOpen; }
    public void setOpen(boolean open) { isOpen = open; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public Instant getCreatedAt() { return createdAt; }
    public void setMinimumOrderAmount(double minimumOrderAmount) { this.minimumOrderAmount = minimumOrderAmount; }
    public double getMinimumOrderAmount() { return minimumOrderAmount; }

}