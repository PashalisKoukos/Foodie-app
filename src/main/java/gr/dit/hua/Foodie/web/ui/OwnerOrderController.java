package gr.dit.hua.Foodie.web.ui;

import gr.dit.hua.Foodie.core.service.OrderService;
import gr.dit.hua.Foodie.core.service.RestaurantService;
import gr.dit.hua.Foodie.core.service.model.RestaurantView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC controller for viewing and managing incoming restaurant orders under {@code /owner/orders}.
 */
@Controller
@RequestMapping("/owner/orders")
public class OwnerOrderController {

    private static final Logger log = LoggerFactory.getLogger(OwnerOrderController.class);

    private final RestaurantService restaurantService;
    private final OrderService orderService;

    public OwnerOrderController(RestaurantService restaurantService, OrderService orderService) {
        this.restaurantService = restaurantService;
        this.orderService = orderService;
    }

    /**
     * Renders the orders management page for the restaurant belonging to the authenticated owner.
     *
     * @param authentication current security authentication providing the owner's username
     * @param model          Spring MVC model populated with the restaurant and its orders
     * @return the view name for the owner orders page
     */
    @GetMapping
    public String ordersPage(Authentication authentication, Model model) {
        String username = authentication.getName();
        RestaurantView restaurant = restaurantService.getRestaurantByOwnerUsername(username);
        model.addAttribute("restaurant", restaurant);
        // TODO: Wire orderService.getOrdersByRestaurantId(restaurant.id()) once ready
        model.addAttribute("orders", java.util.List.of());
        return "owner/orders";
    }

    /**
     * Accepts a pending order belonging to the authenticated owner's restaurant.
     *
     * @param orderId            identifier of the order to accept
     * @param authentication     current security authentication providing the owner's username
     * @param redirectAttributes flash attributes for feedback across redirect
     * @return redirect back to {@code /owner/orders}
     */
    @PostMapping("/{orderId}/accept")
    public String acceptOrder(@PathVariable Long orderId, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        try {
            // TODO: Delegate acceptance to OrderService with ownership & status checks (!)
            redirectAttributes.addFlashAttribute("successMessage", "Order #" + orderId + " accepted.");
        } catch (RuntimeException ex) {
            log.warn("Failed to accept order {} by owner '{}': {}", orderId, username, ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/owner/orders";
    }

    /**
     * Rejects a pending order belonging to the authenticated owner's restaurant.
     *
     * @param orderId            identifier of the order to reject
     * @param authentication     current security authentication providing the owner's username
     * @param redirectAttributes flash attributes for feedback across redirect
     * @return redirect back to {@code /owner/orders}
     */
    @PostMapping("/{orderId}/reject")
    public String rejectOrder(@PathVariable Long orderId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        try {
            // TODO: Delegate rejection to OrderService with ownership & status checks
            redirectAttributes.addFlashAttribute("successMessage", "Order #" + orderId + " rejected.");
        } catch (RuntimeException ex) {
            log.warn("Failed to reject order {} by owner '{}': {}", orderId, username, ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/owner/orders";
    }

    /**
     * Updates the status of an order belonging to the authenticated owner's restaurant.
     *
     * @param orderId            identifier of the order to update
     * @param status             target status value submitted by the form
     * @param authentication     current security authentication providing the owner's username
     * @param redirectAttributes flash attributes for feedback across redirect
     * @return redirect back to {@code /owner/orders}
     */
    @PostMapping("/{orderId}/status")
    public String updateStatus(@PathVariable Long orderId,
                               @RequestParam("status") String status,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        try {
            // TODO: Delegate status update to OrderService (and block edits when in delivery)
            redirectAttributes.addFlashAttribute("successMessage", "Order #" + orderId + " updated to " + status + ".");
        } catch (RuntimeException ex) {
            log.warn("Failed to update status for order {} to '{}' by owner '{}': {}", orderId, status, username, ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/owner/orders";
    }
}