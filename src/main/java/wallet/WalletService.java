package wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import pl.muybien.marketsync.asset.AssetProvider;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final AssetProvider assetProvider;

//    public void addNewAssetToWallet(OidcUser oidcUser, String uri, BigDecimal count) {
//        BigDecimal currentPrice =  assetProvider.fetchAsset(uri).getPriceUsd();
//        Wallet wallet = Wallet.builder()
//                .assetName(uri)
//                .count(count)
//                .currentPrice(currentPrice)
//                .investmentPeriod()
//                .profitInPercentage()
//                .creationDate()
//                .averagePurchasePrice()
//                .customerEmail(oidcUser.getEmail())
//                .build();
//    }
}