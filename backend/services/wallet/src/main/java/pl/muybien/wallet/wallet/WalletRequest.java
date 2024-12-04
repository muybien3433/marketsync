package pl.muybien.wallet.wallet;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record WalletRequest(

        String authorizationHeader,

        @NotNull(message = "Customer should be present")
        Long customerId

) {
}
