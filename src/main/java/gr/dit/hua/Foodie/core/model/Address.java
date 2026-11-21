package gr.dit.hua.Foodie.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

// A saved delivery address of a customer. Each address belongs to exactly one user.
@Entity
@Table(name = "foodie_address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // The customer who owns this address
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // A short name chosen by the customer, e.g. "Home" or "Work"
    @NotBlank(message = "Label is required")
    @Column(name = "label", nullable = false, length = 50)
    private String label;

    @NotBlank(message = "Street is required")
    @Column(name = "street", nullable = false, length = 100)
    private String street;

    @NotBlank(message = "City is required")
    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @NotBlank(message = "Postal code is required")
    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Required by JPA
    protected Address() {
    }

    public Address(User user, String label, String street, String city, String postalCode) {
        this.user = user;
        this.label = label;
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
    }

    // The text that is copied into an order as its delivery address
    public String toDeliveryText() {
        return street + ", " + city + " " + postalCode;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public Instant getCreatedAt() { return createdAt; }
}