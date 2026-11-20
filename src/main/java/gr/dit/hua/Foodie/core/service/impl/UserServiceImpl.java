package gr.dit.hua.Foodie.core.service.impl;

import gr.dit.hua.Foodie.core.model.Role;
import gr.dit.hua.Foodie.core.model.User;
import gr.dit.hua.Foodie.core.port.EmailNotificationPort;
import gr.dit.hua.Foodie.core.port.PhoneNumberPort;
import gr.dit.hua.Foodie.core.port.SmsNotificationPort;
import gr.dit.hua.Foodie.core.port.dto.PhoneNumberValidationResult;
import gr.dit.hua.Foodie.core.repository.UserRepository;
import gr.dit.hua.Foodie.core.service.RegistrationService;
import gr.dit.hua.Foodie.core.service.model.RegistrationRequest;
import gr.dit.hua.Foodie.core.service.model.RegistrationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements RegistrationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class) ;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SmsNotificationPort smsNotificationPort ;
    private final PhoneNumberPort phoneNumberPort;
    private final EmailNotificationPort emailNotificationPort ;



    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           PhoneNumberPort phoneNumberPort, SmsNotificationPort smsNotificationPort, EmailNotificationPort  emailNotificationPort) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.phoneNumberPort = phoneNumberPort ;
        this.smsNotificationPort = smsNotificationPort ;
        this.emailNotificationPort = emailNotificationPort ;
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

        // Έλεγχος κινητού & κανονικοποίηση σε E.164
        final PhoneNumberValidationResult phoneValidation;
        try {
            phoneValidation = phoneNumberPort.validate(request.mobilePhoneNumber());
        } catch (Exception e) {
            LOGGER.error("Failed to validate mobile phone number for user {}", request.username(), e);
            return RegistrationResult.failure("Could not validate mobile phone number!");
        }

        if (phoneValidation == null || !phoneValidation.isValidMobile()) {
            return RegistrationResult.failure("Invalid mobile phone number!");
        }

        final String e164Phone = (phoneValidation.e164() != null && !phoneValidation.e164().isBlank())
                ? phoneValidation.e164()
                : request.mobilePhoneNumber();

        // Έλεγχoς μοναδικότητας και για το κανονικοποιημένο e164Phone:
        if (!e164Phone.equals(request.mobilePhoneNumber()) && userRepository.existsByMobilePhoneNumber(e164Phone)) {
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
        try {
            final boolean sent = smsNotificationPort.sendSms(e164Phone, "Your account on Foodie-App was successfully created.");
            if (!sent) {
                LOGGER.warn("Welcome SMS was not sent to user {} ({})", user.getUsername(), e164Phone);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to send welcome SMS to user {} ({})", user.getUsername(), e164Phone, e);
        }

        try {
            emailNotificationPort.sendEmail(
                    user.getEmail(),
                    "Welcome to Foodie-App",
                    "Your account on Foodie-App was successfully created."
            );
        } catch (Exception e) {
            LOGGER.error("Failed to send welcome email to user {} ({})", user.getUsername(), user.getEmail(), e);
        }

        return RegistrationResult.success();
    }
}