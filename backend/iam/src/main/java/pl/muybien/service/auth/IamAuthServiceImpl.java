package pl.muybien.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import pl.muybien.dto.request.IamUserLoginRequest;
import pl.muybien.dto.response.IamUserLoginResponse;
import pl.muybien.exception.IamLoginException;
import pl.muybien.exception.KeycloakErrorResponse;

@Service
public class IamAuthServiceImpl implements IamAuthService {

    private final WebClient webClient;
    private final String clientId;
    private final String clientSecret;
    private final String tokenUrl;

    public IamAuthServiceImpl(
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

    @Override
    public IamUserLoginResponse loginUser(IamUserLoginRequest request) {
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
                                    .map(error ->
                                            new IamLoginException("Keycloak server error during login", clientResponse.statusCode().value())
                                    )
                    )
                    .bodyToMono(IamUserLoginResponse.class)
                    .block();
        } catch (WebClientResponseException ex) {
            throw new IamLoginException("Error calling Keycloak token endpoint. Status: " + ex.getStatusCode().value(), ex.getStatusCode().value());
        } catch (Exception ex) {
            throw new IamLoginException("Unexpected error during login: " + ex.getMessage(), 500);
        }
    }

    private IamLoginException map4xxErrorToException(KeycloakErrorResponse error, int status) {
        String errorCode = error.getError();
        String description = error.getErrorDescription();

        if ("invalid_grant".equals(errorCode)) {
            return new IamLoginException(description != null ? description : "Invalid username or password", 401);
        }

        if ("invalid_client".equals(errorCode)) {
            return new IamLoginException("Invalid client configuration for Keycloak (invalid_client)", 500);
        }

        if ("unauthorized_client".equals(errorCode)) {
            return new IamLoginException("Client is not allowed to use this grant type (unauthorized_client)", 500);
        }

        if ("unsupported_grant_type".equals(errorCode)) {
            return new IamLoginException("Unsupported grant type configured for Keycloak token endpoint", 500);
        }

        if ("invalid_request".equals(errorCode)) {
            return new IamLoginException(
                    description != null ? description : "Invalid login request sent to Keycloak",
                    status
            );
        }

        return new IamLoginException(
                description != null ? description : "Unexpected client error from Keycloak: " + errorCode,
                status
        );
    }
}