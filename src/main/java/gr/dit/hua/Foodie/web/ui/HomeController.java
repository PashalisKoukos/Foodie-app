package gr.dit.hua.Foodie.web.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Shows the home page. "/" is public, and "/home" is where the user
// lands after a successful login (see SecurityConfig).
@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home() {
        return "home"; // renders templates/home.html
    }
}