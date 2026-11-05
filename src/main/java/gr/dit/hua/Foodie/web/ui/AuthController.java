package gr.dit.hua.Foodie.web.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * UI controller for handling authentication views (Login).
 */
@Controller
public class AuthController {

    /**
     * Redirect root URL ("/") to the login page.
     */
    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    /**
     * Displays the custom login page.
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Resolves to login.html template
    }
}