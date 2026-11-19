package gr.dit.hua.Foodie.core.port;

import java.util.Optional ;
import gr.dit.hua.Foodie.core.model.Role ;


public interface LookupPort {

    Optional<Role> lookup(final String username);
}
