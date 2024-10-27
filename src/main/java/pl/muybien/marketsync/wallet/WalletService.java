package pl.muybien.marketsync.wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.marketsync.finance.FinanceProvider;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final FinanceProvider financeProvider;

//    @Transactional
//    public void addNewAssetToWallet(OidcUser oidcUser, String uri, BigDecimal count) {
//        BigDecimal currentPrice =  assetProvider.fetchFinance(uri).getPriceUsd();
//        Wallet wallet = Wallet.builder()
//                .assetName(uri.toLowerCase())
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