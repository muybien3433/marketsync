package pl.muybien.wallet.wallet;

import lombok.Builder;

@Builder
public record WalletRequest(

        String authorizationHeader
) {
}
