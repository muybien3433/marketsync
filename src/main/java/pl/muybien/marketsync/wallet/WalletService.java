package pl.muybien.marketsync.wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.muybien.marketsync.finance.FinanceProvider;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final FinanceProvider financeProvider;

//    public void addNewAssetToWallet(OidcUser oidcUser, String uri, BigDecimal count) {
//        BigDecimal currentPrice =  assetProvider.fetchFinance(uri).getPriceUsd();
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