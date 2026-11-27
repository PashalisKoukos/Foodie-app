package gr.dit.hua.Foodie.web.ui;

import gr.dit.hua.Foodie.core.service.RestaurantItemService;
import gr.dit.hua.Foodie.core.service.RestaurantService;
import gr.dit.hua.Foodie.core.service.model.RestaurantView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

/**
 * MVC controller for managing the authenticated restaurant owner's menu items,
 * prices, and availability under {@code /owner/menu}.
 */
@Controller
@RequestMapping("/owner/menu")
public class OwnerMenuController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerMenuController.class);
    private final RestaurantService restaurantService;
    private final RestaurantItemService restaurantItemService;

    public OwnerMenuController(RestaurantService restaurantService, RestaurantItemService restaurantItemService) {
        this.restaurantService = restaurantService;
        this.restaurantItemService = restaurantItemService;
    }

    /**
     * Renders the menu management page for the restaurant belonging to the current owner.
     *
     * @param authentication current security authentication providing the owner's username
     * @param model          Spring MVC model populated with the restaurant and its menu items
     * @return the view name for the owner menu page
     */
    @GetMapping
    public String menuPage(Authentication authentication, Model model) {
        String username = authentication.getName();
        RestaurantView restaurant = restaurantService.getRestaurantByOwnerUsername(username);
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("items", restaurantItemService.getItemsByRestaurantId(restaurant.id()));
        return "owner/menu";
    }

    /**
     * Adds a new menu item to the authenticated owner's restaurant.
     *
     * @param name               name of the new menu item
     * @param description        optional description of the item
     * @param price              item price
     * @param available          initial availability flag (defaults to {@code true})
     * @param authentication     current security authentication providing the owner's username
     * @param redirectAttributes flash attributes for success or error feedback across redirect
     * @return redirect back to {@code /owner/menu}
     */
    @PostMapping("/items")
    public String addItem(@RequestParam("name") String name,
                          @RequestParam(name = "description", required = false) String description,
                          @RequestParam("price") BigDecimal price,
                          @RequestParam(name = "available", defaultValue = "true") boolean available,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        try {
            Long restaurantId = restaurantService.getRestaurantByOwnerUsername(username).id();
            restaurantItemService.addItem(restaurantId, username, name, description, price, available);
            redirectAttributes.addFlashAttribute("successMessage", "Menu item added.");
        } catch (RuntimeException ex) {
            LOGGER.warn("Failed to add menu item '{}' for owner '{}': {}", name, username, ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/owner/menu";
    }

    /**
     * Updates the price of an existing menu item owned by the current user.
     *
     * @param itemId             identifier of the menu item to update
     * @param price              new price value
     * @param authentication     current security authentication providing the owner's username
     * @param redirectAttributes flash attributes for success or error feedback across redirect
     * @return redirect back to {@code /owner/menu}
     */
    @PostMapping("/items/{itemId}/price")
    public String updatePrice(@PathVariable Long itemId,
                              @RequestParam("price") BigDecimal price,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        try {
            restaurantItemService.updatePrice(itemId, username, price);
            redirectAttributes.addFlashAttribute("successMessage", "Price updated.");
        } catch (RuntimeException ex) {
            LOGGER.warn("Failed to update price for item {} by owner '{}': {}", itemId, username, ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/owner/menu";
    }

    /**
     * Updates the availability status (available/unavailable) of a menu item.
     *
     * @param itemId             identifier of the menu item to update
     * @param available          target availability state
     * @param authentication     current security authentication providing the owner's username
     * @param redirectAttributes flash attributes for success or error feedback across redirect
     * @return redirect back to {@code /owner/menu}
     */
    @PostMapping("/items/{itemId}/availability")
    public String changeAvailability(@PathVariable Long itemId,
                                     @RequestParam("available") boolean available,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        try {
            restaurantItemService.setAvailability(itemId, username, available);
            redirectAttributes.addFlashAttribute("successMessage", "Availability updated.");
        } catch (RuntimeException ex) {
            LOGGER.warn("Failed to change availability for item {} by owner '{}': {}", itemId, username, ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/owner/menu";
    }

    /**
     * Deletes a menu item belonging to the authenticated owner's restaurant.
     *
     * @param itemId             identifier of the menu item to delete
     * @param authentication     current security authentication providing the owner's username
     * @param redirectAttributes flash attributes for success or error feedback across redirect
     * @return redirect back to {@code /owner/menu}
     */
    @PostMapping("/items/{itemId}/delete")
    public String deleteItem(@PathVariable Long itemId, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        try {
            restaurantItemService.deleteItem(itemId, username);
            redirectAttributes.addFlashAttribute("successMessage", "Item deleted.");
        } catch (RuntimeException ex) {
            LOGGER.warn("Failed to delete item {} by owner '{}': {}", itemId, username, ex.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/owner/menu";
    }
}