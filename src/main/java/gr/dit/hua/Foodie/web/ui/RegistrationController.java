package gr.dit.hua.Foodie.web.ui;

import gr.dit.hua.Foodie.core.service.impl.UserServiceImpl;
import gr.dit.hua.Foodie.core.service.model.RegistrationRequest;
import gr.dit.hua.Foodie.core.service.model.RegistrationResult;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationController.class);
    private final UserServiceImpl userService;

    public RegistrationController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(final Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest("", "", "", ""));
        return "register";
    }

    @PostMapping("/register")
    public String handleFormSubmission(
            @Valid @ModelAttribute("registrationRequest") final RegistrationRequest registrationRequest,
            BindingResult bindingResult,
            final Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Κλήση του service που επιστρέφει RegistrationResult
        RegistrationResult registrationResult = this.userService.register(registrationRequest);

        if (registrationResult.created()) {
            LOGGER.info("User created successfully: {}", registrationRequest.username());
            return "redirect:/login?success";
        }

        // Αν απέτυχε, περνάμε το μήνυμα λάθους (reason) πίσω στη φόρμα
        model.addAttribute("registrationRequest", registrationRequest);
        model.addAttribute("errorMessage", registrationResult.reason());
        return "register";
    }
}