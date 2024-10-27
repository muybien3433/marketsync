package pl.muybien.marketsync.asset;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.marketsync.customer.CustomerService;
import pl.muybien.marketsync.finance.FinanceProviderFactory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final CustomerService customerService;
    private final FinanceProviderFactory financeProviderFactory;
    private final AssetRepository assetRepository;
    private final AssetDTOMapper assetDTOMapper;


    public void createNewAsset(OidcUser oidcUser, String uri, AssetRequest request) {
        // TODO: After marketplace creation provide necessarily logic switching between providers
        var financeProvider = financeProviderFactory.getProvider("crypto");
        BigDecimal currentAssetPrice = financeProvider.fetchFinance(uri).getPriceUsd();

        var customer = customerService.findCustomerByEmail(oidcUser.getEmail());
        var wallet = customer.getWallet();
        BigDecimal value = calculateAssetValue(currentAssetPrice, request.count());

        assetRepository.save(
                Asset.builder()
                        .name(uri.toLowerCase())
                        .value(value)
                        .count(request.count())
                        .averagePurchasePrice(currentAssetPrice)
                        .purchaseCount(1) // TODO: probably needs to be in Wallet which needs to calculate all assets by name
                        .currentPrice(currentAssetPrice)
                        .investmentPeriodInDays(1) // auto-incremented every day
                        .profitInPercentage(BigDecimal.valueOf(0))
                        .profit(BigDecimal.valueOf(0))
                        .wallet(wallet)
                        .build()
        );
    }

    private BigDecimal calculateAssetValue(BigDecimal currentAssetPrice, BigDecimal count) {
        return currentAssetPrice.multiply(count);
    }

    @Transactional(readOnly = true)
    public List<AssetDTO> findAllWalletAssets(OidcUser oidcUser) {
        var customer = customerService.findCustomerByEmail(oidcUser.getEmail());
        var wallet = customer.getWallet();
        var assets = assetRepository.findAllAssetsByWalletId(wallet.getId())
                .orElse(Collections.emptyList());

        return assets.stream()
                .map(assetDTOMapper::mapToDTO)
                .collect(Collectors.toList());
    }
}