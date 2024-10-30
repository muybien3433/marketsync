package pl.muybien.marketsync.wallet;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.marketsync.asset.Asset;
import pl.muybien.marketsync.finance.FinanceProvider;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final FinanceProvider financeProvider;

    @Transactional
    public void addAssetToWallet(OidcUser oidcUser, Wallet wallet, Asset asset) {

    }
}