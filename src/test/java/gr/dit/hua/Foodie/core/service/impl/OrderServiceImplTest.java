package gr.dit.hua.Foodie.core.service.impl;

import gr.dit.hua.Foodie.core.model.*;
import gr.dit.hua.Foodie.core.repository.OrderRepository;
import gr.dit.hua.Foodie.core.repository.RestaurantItemRepository;
import gr.dit.hua.Foodie.core.repository.RestaurantRepository;
import gr.dit.hua.Foodie.core.repository.UserRepository;
import gr.dit.hua.Foodie.core.service.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the business rules of OrderServiceImpl.
 * The repositories are mocks, so no database is needed and the tests run in milliseconds.
 * Rules covered: 1 (closed restaurant), 2 (minimum amount), 3 and 5 (legal status changes), 4 (availability).
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private RestaurantItemRepository itemRepository;
    @Mock private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    // ---------- helpers ----------

    private User customer() {
        return new User("maria", "maria@test.com", "+306900000001", "hash", Role.CUSTOMER);
    }

    private Restaurant restaurant(boolean open, String minimumOrder) {
        User owner = new User("owner1", "owner1@test.com", "+306900000002", "hash", Role.OWNER);
        return new Restaurant("Test Greek House", "Greek", "Athens", "Test Street 1",
                open, new BigDecimal(minimumOrder), owner);
    }

    private RestaurantItem item(Restaurant restaurant, String price, boolean available) {
        return new RestaurantItem("Moussaka", "Eggplant and beef", new BigDecimal(price),
                available, restaurant);
    }

    // a customer and a restaurant that the service can find
    private Restaurant stubCustomerAndRestaurant(boolean open, String minimumOrder) {
        Restaurant restaurant = restaurant(open, minimumOrder);
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer()));
        when(restaurantRepository.findById(10L)).thenReturn(Optional.of(restaurant));
        return restaurant;
    }

    //Rule 1: closed restaurant

    @Test
    void closedRestaurantRejectsOrder() {
        stubCustomerAndRestaurant(false, "5.00");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(1L, 10L, List.of(100L), OrderType.PICKUP, null));

        assertTrue(ex.getMessage().contains("not open"));
        verify(orderRepository, never()).save(any());   // nothing was written
    }

    //Rule 2: minimum order amount (invalid and valid)

    @Test
    void amountBelowMinimumRejectsOrder() {
        Restaurant restaurant = stubCustomerAndRestaurant(true, "10.00");
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item(restaurant, "4.00", true)));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(1L, 10L, List.of(100L), OrderType.PICKUP, null));

        assertTrue(ex.getMessage().contains("too low"));
        verify(orderRepository, never()).save(any());
    }

    @Test
    void amountEqualToMinimumIsAccepted() {
        Restaurant restaurant = stubCustomerAndRestaurant(true, "10.00");
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item(restaurant, "10.00", true)));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        orderService.createOrder(1L, 10L, List.of(100L), OrderType.PICKUP, null);

        ArgumentCaptor<Order> saved = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(saved.capture());
        assertEquals(0, new BigDecimal("10.00").compareTo(saved.getValue().getTotalPrice()));
        assertEquals(OrderStatus.PENDING, saved.getValue().getStatus());
    }

    //Rule 4: unavailable item

    @Test
    void unavailableItemRejectsOrder() {
        Restaurant restaurant = stubCustomerAndRestaurant(true, "5.00");
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item(restaurant, "12.00", false)));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(1L, 10L, List.of(100L), OrderType.PICKUP, null));

        assertTrue(ex.getMessage().contains("not available"));
        verify(orderRepository, never()).save(any());
    }

    //Rules 3 and 5: legal status changes

    private Order orderWithStatus(OrderStatus status) {
        Order order = new Order();
        order.setStatus(status);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        return order;
    }

    @Test
    void illegalStatusChangeIsRejected() {
        orderWithStatus(OrderStatus.PENDING);

        // a new order cannot jump straight to DELIVERED
        assertThrows(RuntimeException.class, () -> orderService.updateOrderStatus(1, OrderStatus.DELIVERED));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void orderOutForDeliveryCannotBeCancelledOrModified() {
        Order order = orderWithStatus(OrderStatus.OUT_FOR_DELIVERY);

        assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus(1, OrderStatus.CANCELLED));
        assertThrows(RuntimeException.class,
                () -> orderService.updateOrderStatus(1, OrderStatus.PREPARING));

        assertEquals(OrderStatus.OUT_FOR_DELIVERY, order.getStatus()); // unchanged
        verify(orderRepository, never()).save(any());
    }

    @Test
    void legalStatusChangeIsAccepted() {
        Order order = orderWithStatus(OrderStatus.PENDING);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        orderService.updateOrderStatus(1, OrderStatus.ACCEPTED);

        assertEquals(OrderStatus.ACCEPTED, order.getStatus());
    }

}
