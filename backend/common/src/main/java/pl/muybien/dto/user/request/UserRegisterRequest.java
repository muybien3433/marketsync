package pl.muybien.dto.user.request;

import pl.muybien.dto.iam.request.KeycloakUserCreateRequest;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.LanguageType;

public record UserRegisterRequest(
        LanguageType language,
        CurrencyType currency,
        KeycloakUserCreateRequest keycloakUserCreateRequest
) {
}
