package gr.dit.hua.Foodie.core.port;

import gr.dit.hua.Foodie.core.port.dto.PhoneNumberValidationResult;

public interface PhoneNumberPort {

    PhoneNumberValidationResult validate(final String rawPhoneNumber);
}
