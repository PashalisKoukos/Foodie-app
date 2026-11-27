package gr.dit.hua.Foodie.web.ui;

import gr.dit.hua.Foodie.core.model.OrderType;
import gr.dit.hua.Foodie.core.service.OrderService;
import gr.dit.hua.Foodie.core.service.RestaurantItemService;
import gr.dit.hua.Foodie.core.service.RestaurantService;
import gr.dit.hua.Foodie.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * MVC controller for customer browsing and order placement under {@code /customer}.
 */
@Controller
@RequestMapping("/customer")
public class CustomerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);
    private final RestaurantService restaurantService;
    private final RestaurantItemService restaurantItemService;
    private final OrderService orderService;
    private final UserService userService;

    public CustomerController(RestaurantService restaurantService, RestaurantItemService restaurantItemService, OrderService orderService, UserService userService) {
        this.restaurantService = restaurantService;
        this.restaurantItemService = restaurantItemService;
        this.orderService = orderService;
        this.userService = userService;
    }

    /**
     * Renders the customer home page with available restaurants and, when a restaurant ID
     * is provided, the selected restaurant along with its menu items.
     *
     * @param restaurantId optional ID of the restaurant whose menu should be displayed
     * @param model        Spring MVC model populated with restaurants and optional menu data
     * @return the view name for the customer home page
     */
    @GetMapping("/customer_page")
    public String home(@RequestParam(name = "restaurantId", required = false) Long restaurantId, Model model) {
        model.addAttribute("restaurants", restaurantService.getAllRestaurants());

        if (restaurantId != null) {
            restaurantService.getRestaurantById(restaurantId).ifPresent(restaurant -> {
                model.addAttribute("selectedRestaurant", restaurant);
                model.addAttribute("menuItems", restaurantItemService.getItemsByRestaurantId(restaurantId));
            });
        }

        return "customer/customer_page";
    }

    /**
     * Places a new order for the authenticated customer at the specified restaurant.
     *
     * @param restaurantId    ID of the target restaurant
     * @param itemIds         selected menu item IDs to include in the order
     * @param orderType       fulfillment type (defaults to {@link OrderType#PICKUP})
     * @param deliveryAddress optional delivery address when placing a delivery order
     * @param authentication  current security authentication providing the customer's username
     * @param model           Spring MVC model populated with page data and status/error messages
     * @return the view name for the customer home page
     */
    @PostMapping("/order")
    public String placeOrder(@RequestParam("restaurantId") Long restaurantId,
                             @RequestParam(name = "itemIds", required = false) List<Long> itemIds,
                             @RequestParam(name = "orderType", defaultValue = "PICKUP") OrderType orderType,
                             @RequestParam(name = "deliveryAddress", required = false) String deliveryAddress,
                             Authentication authentication, Model model) {

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

        String username = authentication.getName();
        try {
            Long customerId = userService.getUserIdByUsername(username);
            // TODO: Courier integration belongs inside OrderService rather than the controller API EXTERNAL CALL (?)
            orderService.createOrder(customerId, restaurantId, itemIds, orderType, deliveryAddress);
            model.addAttribute("successMessage", "Order placed successfully!");
        } catch (RuntimeException ex) {
            LOGGER.warn("Failed to place order for customer '{}' at restaurant {}: {}", username, restaurantId, ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());
        }

        return "customer/customer_page";
    }
}