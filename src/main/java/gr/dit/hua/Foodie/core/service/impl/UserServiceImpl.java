package gr.dit.hua.Foodie.core.service.impl;

import gr.dit.hua.Foodie.core.model.Role;
import gr.dit.hua.Foodie.core.model.User;
import gr.dit.hua.Foodie.core.repository.UserRepository;
import gr.dit.hua.Foodie.core.service.RegistrationService;
import gr.dit.hua.Foodie.core.service.mapper.UserMapper;
import gr.dit.hua.Foodie.core.service.model.RegistrationRequest;
import gr.dit.hua.Foodie.core.service.model.RegistrationResult;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public RegistrationResult register(RegistrationRequest request) {
        // Έλεγχοι μοναδικότητας
        if (userRepository.existsByUsername(request.username())) {
            return RegistrationResult.failure("Username already exists!");
        }
        if (userRepository.existsByEmail(request.email())) {
            return RegistrationResult.failure("Email already exists!");
        }
        if (userRepository.existsByMobilePhoneNumber(request.mobilePhoneNumber())) {
            return RegistrationResult.failure("Mobile phone number already exists!");
        }


        User user = new User(
                request.username(),
                request.email(),
                request.mobilePhoneNumber(),
                passwordEncoder.encode(request.rawPassword()),
                Role.CUSTOMER
        );

        userRepository.save(user);


        return RegistrationResult.success();
    }
}