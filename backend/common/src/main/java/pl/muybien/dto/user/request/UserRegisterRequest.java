package pl.muybien.dto.user.request;

import jakarta.validation.constraints.NotNull;
import pl.muybien.dto.iam.request.KeycloakUserCreateRequest;
import pl.muybien.enumeration.CurrencyType;
import pl.muybien.enumeration.LanguageType;

public record UserRegisterRequest(

        @NotNull(message = "Language cannot be null")
        LanguageType language,

        @NotNull(message = "Currency cannot be null")
        CurrencyType currency,

        @NotNull(message = "User create request cannot be null")
        KeycloakUserCreateRequest userCreateRequest
) {
}
