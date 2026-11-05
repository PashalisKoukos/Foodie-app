package gr.dit.hua.Foodie.web.ui;

import gr.dit.hua.Foodie.core.model.Role;
import gr.dit.hua.Foodie.core.model.User;
import gr.dit.hua.Foodie.core.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

/**
 * UI controller for managing user registration.
 */
@Controller
public class RegistrationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public RegistrationController(final UserRepository userRepository) {
        if (userRepository == null) {
            throw new NullPointerException("UserRepository cannot be null");
        }
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String showRegistrationForm(final Model model) {
        // Create an empty User instance for the Thymeleaf form mapping
        final User user = new User(
                null,
                "", "",
                "",
                Role.CUSTOMER
        );
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String handleFormSubmission(@ModelAttribute("user") User user) {
        // Extract fields for validation checks
        final String username = user.getUsername();
        final String email = user.getEmail();
        final String mobilePhoneNumber = user.getMobilePhoneNumber();

        // Check if username already exists in the database
        if (this.userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists!");
        }

        // Check if email address already exists in the database
        if (this.userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email address already exists!");
        }

        // Check if mobile phone number already exists in the database
        if (this.userRepository.existsByMobilePhoneNumber(mobilePhoneNumber)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mobile phone number already exists!");
        }

        // Encode password securely using BCrypt before saving
        user.setPasswordHash(this.passwordEncoder.encode(user.getPasswordHash()));

        // Save user entity to db
        user = this.userRepository.save(user);
        LOGGER.info("User created successfully: {}", user.getUsername());

        // Redirect to login page upon successful registration
        return "redirect:/login?success";
    }
}
