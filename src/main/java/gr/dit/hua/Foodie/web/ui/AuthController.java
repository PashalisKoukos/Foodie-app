package gr.dit.hua.Foodie.web.ui;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * UI controller for handling authentication views (Login).
 */
@Controller
public class AuthController {

    @GetMapping("/login")
    public String showLoginForm(final Authentication authentication) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/";
        }
        return "login";
    }
}