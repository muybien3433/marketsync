package pl.muybien.keycloak;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pl.muybien.dto.request.UserLoginRequest;
import pl.muybien.dto.response.UserLoginResponse;
import pl.muybien.dto.response.KeycloakErrorResponse;
import pl.muybien.exception.LoginException;
import pl.muybien.exception.PasswordChangeException;

@Component
public class KeycloakAuthClient {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;
    private final String tokenUrl;

    public KeycloakAuthClient(
            WebClient.Builder builder,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.server-url}") String keycloakUrl,
            @Value("${keycloak.client-id}") String clientId,
            @Value("${keycloak.client-secret}") String clientSecret
    ) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.webClient = builder.build();
        this.tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    public UserLoginResponse login(UserLoginRequest request) {
        try {
            return webClient.post()
                    .uri(tokenUrl)
                    .body(BodyInserters.fromFormData("grant_type", "password")
                            .with("client_id", clientId)
                            .with("client_secret", clientSecret)
                            .with("username", request.username())
                            .with("password", request.password()))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                            clientResponse.bodyToMono(KeycloakErrorResponse.class).map(error ->
                                    map4xxErrorToException(error, clientResponse.statusCode().value())
                            )
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                            clientResponse.bodyToMono(KeycloakErrorResponse.class)
                                    .defaultIfEmpty(new KeycloakErrorResponse())
                                    .map(_ ->
                                            new LoginException("Keycloak server error during login", clientResponse.statusCode().value())
                                    )
                    )
                    .bodyToMono(UserLoginResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw new LoginException("Error calling Keycloak token endpoint. Status: " + ex.getStatusCode().value(), ex.getStatusCode().value());
        } catch (Exception ex) {
            throw new LoginException("Unexpected error during login: " + ex.getMessage(), 500);
        }
    }

    private LoginException map4xxErrorToException(KeycloakErrorResponse error, int status) {
        String errorCode = error.getError();
        String description = error.getErrorDescription();

        if ("invalid_grant".equals(errorCode)) {
            return new LoginException(description != null ? description : "Invalid username or password", 401);
        }

        if ("invalid_client".equals(errorCode)) {
            return new LoginException("Invalid client configuration for Keycloak (invalid_client)", 500);
        }

        if ("unauthorized_client".equals(errorCode)) {
            return new LoginException("Client not allowed to use this grant type (unauthorized_client)", 500);
        }

        if ("unsupported_grant_type".equals(errorCode)) {
            return new LoginException("Unsupported grant type for token endpoint", 500);
        }

        if ("invalid_request".equals(errorCode)) {
            return new LoginException(description != null ? description : "Invalid login request", status);
        }

        return new LoginException(description != null ? description : "Unexpected client error: " + errorCode, status);
    }

    public void verifyCredentials(String username, String password) {
        try {
            webClient.post()
                    .uri(tokenUrl)
                    .body(BodyInserters.fromFormData("grant_type", "password")
                            .with("client_id", clientId)
                            .with("client_secret", clientSecret)
                            .with("username", username)
                            .with("password", password))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response ->
                            response.bodyToMono(KeycloakErrorResponse.class)
                                    .map(error -> new PasswordChangeException("Invalid current password", 401)))
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception ex) {
            throw new PasswordChangeException("Failed to verify credentials: " + ex.getMessage(), 500);
        }
    }
}
