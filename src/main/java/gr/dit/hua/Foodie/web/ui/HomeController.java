package gr.dit.hua.Foodie.web.ui;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final RestaurantService restaurantService;

    public HomeController(final RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping({"/", "/home"})
    public String home(final Model model) {
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "home";
    }
}