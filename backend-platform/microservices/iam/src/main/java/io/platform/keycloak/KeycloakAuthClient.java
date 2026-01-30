package io.platform.keycloak;

import io.platform.exception.LoginException;
import io.platform.exception.PasswordChangeException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import io.platform.dto.iam.request.KeycloakUserLoginRequest;
import io.platform.dto.iam.response.KeycloakUserLoginResponse;
import io.platform.dto.iam.response.KeycloakErrorResponse;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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

    public KeycloakUserLoginResponse login(KeycloakUserLoginRequest request) {
        try {
            return webClient.post()
                    .uri(tokenUrl)
                    .body(BodyInserters.fromFormData("grant_type", "password")
                            .with("client_id", clientId)
                            .with("client_secret", clientSecret)
                            .with("username", request.username())
                            .with("password", request.password()))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, resp ->
                            resp.bodyToMono(String.class).flatMap(body -> {
                                System.out.println("KEYCLOAK 4XX BODY: " + body);
                                return reactor.core.publisher.Mono.error(
                                        new LoginException(
                                                resp.statusCode().value(),
                                                "LOGIN_KEYCLOAK_4XX",
                                                body
                                        )
                                );
                            })
                    )
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                            clientResponse.bodyToMono(KeycloakErrorResponse.class)
                                    .defaultIfEmpty(new KeycloakErrorResponse())
                                    .map(_ ->
                                            new LoginException(
                                                    502,
                                                    "LOGIN_KEYCLOAK_5XX",
                                                    "Keycloak server error during login"
                                            )
                                    )
                    )
                    .bodyToMono(KeycloakUserLoginResponse.class)
                    .block();
        } catch (LoginException ex) {
            throw ex;
        } catch (WebClientResponseException ex) {
            int status = ex.getStatusCode().value();
            throw new LoginException(
                    status >= 500 ? 502 : status,
                    "LOGIN_TOKEN_ENDPOINT_ERROR",
                    "Error calling Keycloak token endpoint. Status: " + status,
                    ex
            );
        } catch (Exception ex) {
            throw new LoginException(
                    500,
                    "LOGIN_UNEXPECTED",
                    "Unexpected error during login",
                    ex
            );
        }
    }

    private LoginException map4xxErrorToException(KeycloakErrorResponse error, int status) {
        String errorCode = error.getError();
        String description = error.getErrorDescription();

        if ("invalid_grant".equals(errorCode)) {
            return new LoginException(
                    401,
                    "LOGIN_INVALID_GRANT",
                    description != null ? description : "Invalid username or password"
            );
        }

        if ("invalid_client".equals(errorCode)) {
            return new LoginException(
                    502,
                    "LOGIN_INVALID_CLIENT",
                    "Invalid client configuration for Keycloak (invalid_client)"
            );
        }

        if ("unauthorized_client".equals(errorCode)) {
            return new LoginException(
                    502,
                    "LOGIN_UNAUTHORIZED_CLIENT",
                    "Client not allowed to use this grant type (unauthorized_client)"
            );
        }

        if ("unsupported_grant_type".equals(errorCode)) {
            return new LoginException(
                    502,
                    "LOGIN_UNSUPPORTED_GRANT_TYPE",
                    "Unsupported grant type for token endpoint"
            );
        }

        if ("invalid_request".equals(errorCode)) {
            return new LoginException(
                    400,
                    "LOGIN_INVALID_REQUEST",
                    description != null ? description : "Invalid login request"
            );
        }

        return new LoginException(status,
                "LOGIN_CLIENT_ERROR",
                description != null ? description : "Unexpected client error: " + errorCode
        );
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
                                    .map(_ -> new PasswordChangeException(
                                            401,
                                            "PASSWORD_INVALID_CURRENT",
                                            "Invalid current password"
                                    )))
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception ex) {
            throw new PasswordChangeException(
                    500,
                    "PASSWORD_VERIFY_FAILED",
                    "Failed to verify credentials",
                    ex
            );
        }
    }
}
