package gr.dit.hua.Foodie.core.service.impl;

import gr.dit.hua.Foodie.core.model.*;
import gr.dit.hua.Foodie.core.repository.OrderRepository;
import gr.dit.hua.Foodie.core.repository.RestaurantItemRepository;
import gr.dit.hua.Foodie.core.repository.RestaurantRepository;
import gr.dit.hua.Foodie.core.repository.UserRepository;
import gr.dit.hua.Foodie.core.service.OrderService;
import gr.dit.hua.Foodie.core.service.mapper.OrderMapper;
import gr.dit.hua.Foodie.core.service.model.OrderView;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantItemRepository itemRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(RestaurantItemRepository itemRepository, OrderRepository orderRepository,
                            UserRepository userRepository, RestaurantRepository restaurantRepository,
                            OrderMapper orderMapper) {
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderMapper = orderMapper;
    }

    /**
     * method to return and create the order
     * @param customerId to find the customer who made the order
     * @param restaurantId to find the restaurant of the order
     * @param itemIds to find the items in the order
     * @param orderType delivery / pickup
     * @param deliveryAddress if it's delivery , must have an address
     * @return the created order
     */
    @Override
    @Transactional
    public OrderView createOrder(Long customerId, Long restaurantId, List<Long> itemIds, OrderType orderType, String deliveryAddress) {
        /*
         * 1. findCustomer
         * 2. find Restaurant
         * 3. check if the restaurant is open
         * 4. for loop for the price
         * 5. check minimum order amount
         * 6. delivery address if it is delivery
         * 7. create and save order
         * 8. return orderView
         */

        //1
        User customer = userRepository.findById(customerId).orElseThrow(()->new IllegalArgumentException("Customer not found"));

        //2
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(()->new IllegalArgumentException("Restaurant not found"));

        //3
        if(!restaurant.isOpen()) throw new RuntimeException("Restaurant not open. Cannot create order");

        //4
        BigDecimal total = BigDecimal.ZERO;
        ArrayList<OrderItem> items = new ArrayList<>();
        for (Long itemId : itemIds) {
            RestaurantItem item = itemRepository.findById(itemId).orElseThrow(()->new IllegalArgumentException("Item not found"));
            //BR4: an unavailable item cannot be ordered
            if (!item.isAvailable()) throw new RuntimeException("Item not available: " + item.getName());
            // FIX: quantity is 1 for now, because itemIds has no quantities.
            // The price is copied into the line, so later menu price changes do not affect this order.
            items.add(new OrderItem(item, 1, item.getPrice()));
            total = total.add(item.getPrice());
        }

        //5
        if(total.compareTo(restaurant.getMinimumOrderAmount()) < 0) throw new RuntimeException("Order amount too low. Cannot create order"); // FIX: < does not work with BigDecimal

        //6
        if(orderType == OrderType.DELIVERY && (deliveryAddress == null || deliveryAddress.trim().isEmpty())) throw new RuntimeException("Delivery address not found");

        // 7
        Order order = new Order();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        for (OrderItem line : items) {
            order.addItem(line);
        }
        order.setTotalPrice(total);
        order.setStatus(OrderStatus.PENDING);
        order.setOrderType(orderType);
        if(orderType == OrderType.DELIVERY) order.setDeliveryAddress(deliveryAddress);
        else order.setDeliveryAddress(null);

        //8
        return orderMapper.convertOrderToOrderView(orderRepository.save(order));
    }

    /**
     * we get the orders by the customers
     * @param customerId the current customer id
     * @return a list of order views converted by customer id
     */
    @Override
    public List<OrderView> getOrdersByCustomerId(Long customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        List<OrderView> views = new ArrayList<>();

        for (Order order : orders) {
            views.add(orderMapper.convertOrderToOrderView(order));
        }

        return views;
    }

    /**
     * We get the orders by the restaurants
     * @param restaurantId the restaurant id
     * @return a list of order views converted by restaurant id
     */
    @Override
    public List<OrderView> getOrdersByRestaurantId(Long restaurantId) {
        List<Order> orders = orderRepository.findByRestaurantId(restaurantId);
        List<OrderView> views = new ArrayList<>();
        for (Order order : orders) {
            views.add(orderMapper.convertOrderToOrderView(order));
        }
        return views;
    }

    /**
     * method to update an order status
     * @param orderId the id of the current order
     * @param newStatus updated status
     * @return the orderView converted
     * ACTIVE TRANSACTION : if something goes wrong , auto rollback enabled
     */
    @Override
    @Transactional
    public OrderView updateOrderStatus(int orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById((long) orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        // BR3 and BR5: only legal status changes
        if (!order.getStatus().canTransitionTo(newStatus)) throw  new RuntimeException("Illegal status change from " + order.getStatus() + " to " + newStatus);
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        return orderMapper.convertOrderToOrderView(updatedOrder);
    }
}