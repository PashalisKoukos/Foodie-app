package gr.dit.hua.Foodie.core.security;

import gr.dit.hua.Foodie.core.model.Client;
import gr.dit.hua.Foodie.core.repository.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link ClientDetailsService}.
 */
@Service
public class ClientDetailsServiceImpl implements ClientDetailsService{

    private final ClientRepository clientRepository;

    public ClientDetailsServiceImpl(final ClientRepository clientRepository) {
        if (clientRepository == null) throw new NullPointerException();
        this.clientRepository = clientRepository;
    }

    @Override
    public Optional<ClientDetails> authenticate(final String id, final String secret) {
        if (id == null) throw new NullPointerException();
        if (id.isBlank()) throw new IllegalArgumentException();
        if (secret == null) throw new NullPointerException();
        if (secret.isBlank()) throw new IllegalArgumentException();

        final Client client = this.clientRepository.findByName(id).orElse(null);
        if (client == null) {
            return Optional.empty(); // client does not exist.
        }

        if (Objects.equals(client.getSecret(), secret)) {
            // TODO 
            // ClientDetails.id     - map - Client.name
            // ClientDetails.secret - map - Client.secret
            // ClientDetails.roles  - map - Client.permissionsCsv (comma separated values)
            final ClientDetails clientDetails = new ClientDetails(
                    client.getName(),
                    client.getSecret(),
                    Arrays.stream(client.getRolesCsv().split(","))
                            .map(String::strip)
                            .map(String::toUpperCase)
                            .collect(Collectors.toSet()));
            return Optional.of(clientDetails);
        } else {
            return Optional.empty();
        }
    }

}
