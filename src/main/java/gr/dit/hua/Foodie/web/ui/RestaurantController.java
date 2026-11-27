package gr.dit.hua.Foodie.web.ui;

import gr.dit.hua.Foodie.core.service.RestaurantItemService;
import gr.dit.hua.Foodie.core.service.RestaurantService;
import gr.dit.hua.Foodie.core.service.model.RestaurantView;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

// Public pages about restaurants. It talks only to services, never to repositories.
@Controller
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final RestaurantItemService restaurantItemService;

    public RestaurantController(RestaurantService restaurantService, RestaurantItemService restaurantItemService) {
        this.restaurantService = restaurantService;
        this.restaurantItemService = restaurantItemService;
    }

    //the menu of one restaurant, visible to everyone
    @GetMapping("/restaurants/{id}")
    public String menu(@PathVariable Long id, Model model) {
        // the service returns an Optional, so an unknown id becomes a 404 instead of a 500
        RestaurantView restaurant = restaurantService.getRestaurantById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found"));

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("items", restaurantItemService.getItemsByRestaurantId(id));
        return "restaurant_menu";
    }
}