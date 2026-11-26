package gr.dit.hua.Foodie.web.ui;


import gr.dit.hua.Foodie.core.model.Restaurant;
import gr.dit.hua.Foodie.core.security.CurrentUserProvider;
import gr.dit.hua.Foodie.core.service.OrderService;
import gr.dit.hua.Foodie.core.service.RestaurantItemService;
import gr.dit.hua.Foodie.core.service.RestaurantService;
import gr.dit.hua.Foodie.core.service.UserService;
import gr.dit.hua.Foodie.core.service.model.RestaurantView;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/owner")
public class OwnerController {

    private final RestaurantService restaurantService;
    private final RestaurantItemService restaurantItemService;
    private final OrderService orderService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public OwnerController(RestaurantService restaurantService,
                           RestaurantItemService restaurantItemService,
                           OrderService orderService,
                           UserService userService, CurrentUserProvider currentUserProvider) {
        this.restaurantService = restaurantService;
        this.restaurantItemService = restaurantItemService;
        this.orderService = orderService;
        this.userService = userService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/owner_page")
    public String dashboard() {
        return "owner/owner_page";
    }


    @GetMapping("/manage_prices")
    public String managePrices(@RequestParam(required = false) Long restaurantId, Model model) {
        Long ownerId = currentUserProvider.requiredOwnerId();
        RestaurantView ownerRestaurant = restaurantService.getRestaurantByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        // Check that the requested restaurant belongs to the current owner
        if (restaurantId != null && !restaurantId.equals(ownerRestaurant.id())) {
            throw new RuntimeException("You are not allowed to access this restaurant");
        }

        model.addAttribute("restaurant", ownerRestaurant);
        model.addAttribute("menuItems", restaurantItemService.getItemsByRestaurantId(ownerRestaurant.id()));

        return "owner/manage_prices";
    }


}