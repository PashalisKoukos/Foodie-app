package gr.dit.hua.Foodie.web.ui;

import gr.dit.hua.Foodie.core.repository.RestaurantItemRepository;
import gr.dit.hua.Foodie.core.repository.RestaurantRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantItemRepository restaurantItemRepository;

    // restaurantItemRepository μπορεί να διαγραφτεί αργότερα !!
    public CustomerController(RestaurantRepository restaurantRepository,
                              RestaurantItemRepository restaurantItemRepository) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantItemRepository = restaurantItemRepository;
    }

    @GetMapping("/customer_page")
    public String home(@RequestParam(name = "restaurantId", required = false) Long restaurantId,
                       Model model) {
        model.addAttribute("restaurants", restaurantRepository.findAll());

        if (restaurantId != null) {
            restaurantRepository.findById(restaurantId).ifPresent(restaurant -> {
                model.addAttribute("selectedRestaurant", restaurant);
                model.addAttribute("menuItems", restaurantItemRepository.findByRestaurantBelongsToId(restaurantId));
            });
        }

        return "customer/customer_page";


    }
}