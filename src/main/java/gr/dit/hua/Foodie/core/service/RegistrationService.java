package gr.dit.hua.Foodie.core.service;

import gr.dit.hua.Foodie.core.service.model.RegistrationRequest;
import gr.dit.hua.Foodie.core.service.model.RegistrationResult;

/**
 * Service for managing person registration.
 */
public interface RegistrationService {

    RegistrationResult register(final RegistrationRequest request);
}
