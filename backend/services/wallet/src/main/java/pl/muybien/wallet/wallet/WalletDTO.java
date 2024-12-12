package pl.muybien.wallet.wallet;

import lombok.Builder;
import pl.muybien.wallet.asset.AssetDTO;

import java.util.List;

@Builder
public record WalletDTO(
        List<AssetDTO> assets
) {
}
