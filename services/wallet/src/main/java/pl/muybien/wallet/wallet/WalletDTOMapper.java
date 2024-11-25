package pl.muybien.wallet.wallet;

import org.springframework.stereotype.Component;
import pl.muybien.wallet.asset.AssetDTO;

import java.util.stream.Collectors;

@Component
public class WalletDTOMapper {
    WalletDTO mapToDTO(Wallet wallet) {
        return WalletDTO.builder()
                .assets(wallet.getAssets().stream()
                        .map(asset -> AssetDTO.builder()
                                .count(asset.getCount())
                                .averagePurchasePrice(asset.getAveragePurchasePrice())
                                .investmentStartDate(asset.getInvestmentStartDate())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}