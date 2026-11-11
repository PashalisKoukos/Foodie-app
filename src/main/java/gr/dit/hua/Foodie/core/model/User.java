package gr.dit.hua.Foodie.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "foodie_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(name = "username", nullable = false, unique = true, length = 255)
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Please provide a valid email address")
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank(message = "Mobile phone number cannot be blank")
    @Size(min = 10, max = 18, message = "Please provide a valid phone number")
    @Column(name = "mobile_phone_number", nullable = false, length = 18)
    private String mobilePhoneNumber;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public User() {
    }

    public User(String username, String email, String mobilePhoneNumber,
                String passwordHash, Role role) {
        this.username = username;
        this.email = email;
        this.mobilePhoneNumber = mobilePhoneNumber;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobilePhoneNumber() { return mobilePhoneNumber; }
    public void setMobilePhoneNumber(String mobilePhoneNumber) { this.mobilePhoneNumber = mobilePhoneNumber; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPassword() { return passwordHash; }
    public void setPassword(String password) { this.passwordHash = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Instant getCreatedAt() { return createdAt; }
}