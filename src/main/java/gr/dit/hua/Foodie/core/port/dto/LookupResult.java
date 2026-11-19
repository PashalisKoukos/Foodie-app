package gr.dit.hua.Foodie.core.port.dto;
import gr.dit.hua.Foodie.core.model.Role ;


public record LookupResult(
    String raw,
    boolean exists,
    String username,
    Role role
){}
