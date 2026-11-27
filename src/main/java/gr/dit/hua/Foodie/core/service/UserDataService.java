package gr.dit.hua.Foodie.core.service;

import gr.dit.hua.Foodie.core.service.model.UserView;
import java.util.List;

/**
 * Service for managing {@code User} for data analytics purposes.
 */
public interface UserDataService {
    List<UserView> getAllPeople();
}
