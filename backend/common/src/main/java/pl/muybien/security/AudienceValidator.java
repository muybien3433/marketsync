package pl.muybien.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public class AudienceValidator implements OAuth2TokenValidator<Jwt> {

    private final String expectedAudience;

    public AudienceValidator(String expectedAudience) {
        this.expectedAudience = expectedAudience;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        if (expectedAudience == null || expectedAudience.isBlank()) {
            return OAuth2TokenValidatorResult.success();
        }

        List<String> aud = token.getAudience();
        if (aud != null && aud.contains(expectedAudience)) {
            return OAuth2TokenValidatorResult.success();
        }

        String msg = "Invalid audience. Expected: " + expectedAudience + ", actual: " + aud;
        return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", msg, null));
    }
}
