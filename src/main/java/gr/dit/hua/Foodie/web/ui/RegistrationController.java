package gr.dit.hua.Foodie.web.ui;

import gr.dit.hua.Foodie.core.model.Role;
import gr.dit.hua.Foodie.core.model.User;
import gr.dit.hua.Foodie.core.repository.UserRepository;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegistrationForm(final Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String handleFormSubmission(@Valid @ModelAttribute("user") User user,
                                       BindingResult bindingResult,
                                       Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        final String username = user.getUsername();
        final String email = user.getEmail();
        final String mobilePhoneNumber = user.getMobilePhoneNumber();


        if (this.userRepository.existsByUsername(username)) {
            bindingResult.rejectValue("username", "error.user", "Username already exists!");
            return "register";
        }

        if (this.userRepository.existsByEmail(email)) {
            bindingResult.rejectValue("email", "error.user", "Email address already exists!");
            return "register";
        }

        if (this.userRepository.existsByMobilePhoneNumber(mobilePhoneNumber)) {
            bindingResult.rejectValue("mobilePhoneNumber", "error.user", "Mobile phone number already exists!");
            return "register";
        }

        user.setRole(Role.CUSTOMER);
        user.setPasswordHash(this.passwordEncoder.encode(user.getPasswordHash()));

        User savedUser = this.userRepository.save(user);
        LOGGER.info("User created successfully: {}", savedUser.getUsername());

        return "redirect:/login?success";
    }
}