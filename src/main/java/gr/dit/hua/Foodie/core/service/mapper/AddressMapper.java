package gr.dit.hua.Foodie.core.service.mapper;


import gr.dit.hua.Foodie.core.model.Address;
import gr.dit.hua.Foodie.core.service.model.AddressView;
import org.springframework.stereotype.Component;

/**
 * Component responsible for mapping {@link Address} entities to {@link AddressView} representation models.
 */
@Component
public class AddressMapper {

    /**
     * Converts an {@link Address} entity to its corresponding {@link AddressView}.
     *
     * @param address the address entity to convert
     * @return the corresponding {@link AddressView}, or null if the input address is null
     */
    public AddressView convertAddressToAddressView(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressView(
                address.getId(),
                address.getLabel(),
                address.getStreet(),
                address.getCity(),
                address.getPostalCode()
        );
    }
}