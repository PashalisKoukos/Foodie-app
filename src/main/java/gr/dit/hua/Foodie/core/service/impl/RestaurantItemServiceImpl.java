package gr.dit.hua.Foodie.core.service.impl;

import gr.dit.hua.Foodie.core.model.RestaurantItem;
import gr.dit.hua.Foodie.core.repository.RestaurantItemRepository;
import gr.dit.hua.Foodie.core.service.RestaurantItemService;
import gr.dit.hua.Foodie.core.service.mapper.RestaurantItemMapper;
import gr.dit.hua.Foodie.core.service.model.RestaurantItemView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RestaurantItemServiceImpl implements RestaurantItemService {

    private final RestaurantItemRepository itemRepository;
    private final RestaurantItemMapper itemMapper;

    public RestaurantItemServiceImpl(RestaurantItemRepository itemRepository, RestaurantItemMapper itemMapper) {
        this.itemRepository = itemRepository;
        this.itemMapper = itemMapper;
    }

    /**
     * method to get items by the restaurant id
     * @param restaurantId to get the items by the restaurant
     * @return a list with RestaurantItems views
     */
    @Override
    public List<RestaurantItemView> getItemsByRestaurantId(Long restaurantId) {
        List<RestaurantItem> items = itemRepository.findByRestaurantBelongsToId(restaurantId);
        List<RestaurantItemView> views = new ArrayList<>();

        for (RestaurantItem item : items) {
            views.add(itemMapper.convertRestaurantItemToRestaurantItemView(item));
        }

        return views;
    }
}