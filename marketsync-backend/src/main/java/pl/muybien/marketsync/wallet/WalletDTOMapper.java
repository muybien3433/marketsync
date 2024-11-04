package pl.muybien.marketsync.wallet;

import org.springframework.stereotype.Component;
import pl.muybien.marketsync.asset.AssetDTO;

import java.util.stream.Collectors;

@Component
public class WalletDTOMapper {
    WalletDTO mapToDTO(Wallet wallet) {
        return WalletDTO.builder()
                .assets(wallet.getAssets().stream()
                        .map(asset -> AssetDTO.builder()
                                .value(asset.getValue())
                                .count(asset.getCount())
                                .averagePurchasePrice(asset.getAveragePurchasePrice())
                                .investmentPeriodInDays(asset.getInvestmentPeriodInDays())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}