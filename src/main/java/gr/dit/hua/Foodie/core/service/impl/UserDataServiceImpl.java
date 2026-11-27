package gr.dit.hua.Foodie.core.service.impl;

import gr.dit.hua.Foodie.core.model.User;
import gr.dit.hua.Foodie.core.repository.UserRepository;
import gr.dit.hua.Foodie.core.service.UserDataService;
import gr.dit.hua.Foodie.core.service.mapper.UserMapper;
import gr.dit.hua.Foodie.core.service.model.UserView;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default implementation of {@link UserDataService}.
 */
@Service
public class UserDataServiceImpl implements UserDataService {


    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDataServiceImpl(final UserRepository userRepository,
                                 final UserMapper userMapper) {
        if (userRepository == null) throw new NullPointerException();
        if (userMapper == null) throw new NullPointerException();
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserView> getAllPeople() {
        final List<User> userList = this.userRepository.findAll();
        final List<UserView> userViewList = userList
                .stream()
                .map(this.userMapper::convertUserToUserView)
                .toList();
        return userViewList;
    }
}
