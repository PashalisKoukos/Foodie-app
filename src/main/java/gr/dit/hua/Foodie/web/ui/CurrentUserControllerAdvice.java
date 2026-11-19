package gr.dit.hua.Foodie.web.ui;

import gr.dit.hua.Foodie.core.security.CurrentUser;
import gr.dit.hua.Foodie.core.security.CurrentUserProvider;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Provides selected UI controllers' {@link org.springframework.ui.Model} with the current user.
 */
@ControllerAdvice(basePackageClasses = {
        HomeController.class,
        RegistrationController.class,
        CustomerController.class,
        OwnerController.class
})
public class CurrentUserControllerAdvice {

    private final CurrentUserProvider currentUserProvider;

    public CurrentUserControllerAdvice(final CurrentUserProvider currentUserProvider) {
        if (currentUserProvider == null) {
            throw new NullPointerException();
        }
        this.currentUserProvider = currentUserProvider;
    }

    @ModelAttribute("me")
    public CurrentUser addCurrentUserToModel() {
        return this.currentUserProvider.getCurrentUser().orElse(null);
    }
}