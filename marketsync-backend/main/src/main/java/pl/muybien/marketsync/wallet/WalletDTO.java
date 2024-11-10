package pl.muybien.marketsync.wallet;

import lombok.Builder;
import pl.muybien.marketsync.asset.AssetDTO;

import java.util.List;

@Builder
public record WalletDTO(
        List<AssetDTO> assets
) {
}
