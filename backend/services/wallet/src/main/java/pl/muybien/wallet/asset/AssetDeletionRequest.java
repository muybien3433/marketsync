package pl.muybien.wallet.asset;

import jakarta.validation.constraints.NotNull;

public record AssetDeletionRequest(

        @NotNull(message = "Asset should be present")
        Long assetId,

        @NotNull(message = "Customer should be present")
        Long customerId
) {
}
