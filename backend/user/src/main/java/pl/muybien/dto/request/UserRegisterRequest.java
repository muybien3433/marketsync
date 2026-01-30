package pl.muybien.dto.request;

import io.platform.dto.iam.request.KeycloakUserCreateRequest;
import jakarta.validation.constraints.NotNull;
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
