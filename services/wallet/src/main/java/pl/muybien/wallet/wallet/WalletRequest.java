package pl.muybien.wallet.wallet;

import jakarta.validation.constraints.NotNull;

public record WalletRequest(

        @NotNull(message = "Customer should be present")
        Long customerId
) {
}
