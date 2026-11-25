package gr.dit.hua.Foodie.web.ui;

import gr.dit.hua.Foodie.core.model.OrderType;
import gr.dit.hua.Foodie.core.service.OrderService;
import gr.dit.hua.Foodie.core.service.RestaurantItemService;
import gr.dit.hua.Foodie.core.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final RestaurantService restaurantService;
    private final RestaurantItemService restaurantItemService;
    private final OrderService orderService;
    private final UserService userService;

    public CustomerController(RestaurantService restaurantService,
                              RestaurantItemService restaurantItemService,
                              OrderService orderService,
                              UserService userService) {
        this.restaurantService = restaurantService;
        this.restaurantItemService = restaurantItemService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/customer_page")
    public String home(@RequestParam(name = "restaurantId", required = false) Long restaurantId,
                       Model model) {
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());

        if (restaurantId != null) {
            restaurantService.getRestaurantById(restaurantId).ifPresent(restaurant -> {
                model.addAttribute("selectedRestaurant", restaurant);
                model.addAttribute("menuItems", restaurantItemService.getItemsByRestaurantId(restaurantId));
            });
        }

        return "customer/customer_page";
    }

    @PostMapping("/order")
    public String placeOrder(@RequestParam("restaurantId") Long restaurantId, @RequestParam(name = "itemIds", required = false) List<Long> itemIds,
                             @RequestParam(name = "orderType", defaultValue = "PICKUP") OrderType orderType,
                             @RequestParam(name = "deliveryAddress", required = false) String deliveryAddress,
                             Authentication authentication,
                             Model model) {

        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        restaurantService.getRestaurantById(restaurantId).ifPresent(restaurant -> {
            model.addAttribute("selectedRestaurant", restaurant);
            model.addAttribute("menuItems", restaurantItemService.getItemsByRestaurantId(restaurantId));
        });

        if (itemIds == null || itemIds.isEmpty()) {
            model.addAttribute("errorMessage", "Please select at least one item.");
            return "customer/customer_page";
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("errorMessage", "You must be logged in to place an order.");
            return "customer/customer_page";
        }

        try {
            Long customerId = userService.getUserIdByUsername(authentication.getName());
            orderService.createOrder(customerId, restaurantId, itemIds, orderType, deliveryAddress);
            model.addAttribute("successMessage", "Order placed successfully!");
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        return "customer/customer_page";
    }
}