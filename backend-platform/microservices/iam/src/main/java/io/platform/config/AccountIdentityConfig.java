package io.platform.config;

import io.platform.account.UsernameAvailabilityChecker;
import io.platform.keycloak.KeycloakUserClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountIdentityConfig {

    @Bean
    public UsernameAvailabilityChecker usernameAvailabilityChecker(KeycloakUserClient keycloakUserClient) {
        return keycloakUserClient::isUsernameAvailable;
    }
}
