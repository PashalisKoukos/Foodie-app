package gr.dit.hua.Foodie.core.service.mapper;

import gr.dit.hua.Foodie.core.model.User;
import gr.dit.hua.Foodie.core.service.model.UserView;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserView convertUserToUserView(User user) {
        if (user == null) {
            return null;
        }
        return new UserView(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getMobilePhoneNumber(),
                user.getRole()
        );
    }
}