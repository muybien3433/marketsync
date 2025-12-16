package pl.muybien.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.muybien.account.UsernameAvailabilityChecker;
import pl.muybien.keycloak.KeycloakUserClient;

@Configuration
public class AccountIdentityConfig {

    @Bean
    public UsernameAvailabilityChecker usernameAvailabilityChecker(KeycloakUserClient keycloakUserClient) {
        return keycloakUserClient::isUsernameAvailable;
    }
}
