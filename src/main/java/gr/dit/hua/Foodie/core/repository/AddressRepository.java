package gr.dit.hua.Foodie.core.repository;

import gr.dit.hua.Foodie.core.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// Data access for Address. Services must always filter by user, so one customer
// can never reach another customer's addresses.
public interface AddressRepository extends JpaRepository<Address, Long> {

    // All addresses of one customer
    List<Address> findByUserId(Long userId);

    // One address, only if it belongs to the given customer (ownership check, BR7)
    Optional<Address> findByIdAndUserId(Long id, Long userId);
}