package gr.dit.hua.Foodie.core.service.impl;

import gr.dit.hua.Foodie.core.model.Address;
import gr.dit.hua.Foodie.core.model.Role;
import gr.dit.hua.Foodie.core.model.User;
import gr.dit.hua.Foodie.core.repository.AddressRepository;
import gr.dit.hua.Foodie.core.repository.UserRepository;
import gr.dit.hua.Foodie.core.service.AddressService;
import gr.dit.hua.Foodie.core.service.mapper.AddressMapper;
import gr.dit.hua.Foodie.core.service.model.AddressRequest;
import gr.dit.hua.Foodie.core.service.model.AddressView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository,
                              AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.addressMapper = addressMapper;
    }

    /**
     * method to add an address to a customer
     * @param userId the logged-in customer (taken from the session, never from the form)
     * @param request the address data
     * @return the saved address
     */
    @Override
    @Transactional
    public AddressView addAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // only customers have delivery addresses
        if (user.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("Only customers can save addresses");
        }

        Address address = new Address(user, request.label().trim(), request.street().trim(),
                request.city().trim(), request.postalCode().trim());

        return addressMapper.convertAddressToAddressView(addressRepository.save(address));
    }

    /**
     * we get the addresses of one customer
     * @param userId the current customer id
     * @return a list of address views, only of this customer (BR7)
     */
    @Override
    public List<AddressView> getAddressesByUserId(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        List<AddressView> views = new ArrayList<>();

        for (Address address : addresses) {
            views.add(addressMapper.convertAddressToAddressView(address));
        }

        return views;
    }

    /**
     * method to delete an address
     * @param addressId the address to delete
     * @param userId the logged-in customer
     * Old orders keep their own copy of the address text, so deleting is safe.
     */
    @Override
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        // findByIdAndUserId finds nothing for another customer's address,
        // so the response is the same as for an address that does not exist
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        addressRepository.delete(address);
    }
}