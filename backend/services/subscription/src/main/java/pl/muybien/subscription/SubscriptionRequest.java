package pl.muybien.subscription;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SubscriptionRequest(

        @NotNull(message = "Uri is required")
        @NotEmpty(message = "Uri is required")
        @NotBlank(message = "Uri is required")
        String uri,

        @NotNull(message = "Value should be present")
        @Positive(message = "Value should be positive")
        BigDecimal value
) {
}
