package gr.dit.hua.Foodie.core.service;

import gr.dit.hua.Foodie.core.service.model.AddressRequest;
import gr.dit.hua.Foodie.core.service.model.AddressView;

import java.util.List;

public interface AddressService {

    AddressView addAddress(Long userId, AddressRequest request);

    List<AddressView> getAddressesByUserId(Long userId);

    void deleteAddress(Long addressId, Long userId);
}