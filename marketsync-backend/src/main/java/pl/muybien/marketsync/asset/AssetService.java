package pl.muybien.marketsync.asset;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.marketsync.customer.CustomerService;
import pl.muybien.marketsync.finance.FinanceProviderFactory;
import pl.muybien.marketsync.handler.AssetNotFoundException;
import pl.muybien.marketsync.handler.AssetOwnershipException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final CustomerService customerService;
    private final FinanceProviderFactory financeProviderFactory;
    private final AssetRepository assetRepository;
    private final AssetDTOMapper assetDTOMapper;

    @Transactional
    public void createOrUpdateAsset(OidcUser oidcUser, String uri, AssetRequest request) {
        // TODO: After marketplace creation provide necessarily logic switching between providers
        var financeProvider = financeProviderFactory.getProvider("crypto");
        BigDecimal currentAssetPrice = financeProvider.fetchFinance(uri).getPriceUsd();

        var customer = customerService.findCustomerByEmail(oidcUser.getEmail());
        var wallet = customer.getWallet();
        BigDecimal value = calculateAssetValue(currentAssetPrice, request.count());

        var incomingAsset = Asset.builder()
                .name(uri.toLowerCase())
                .value(value)
                .count(request.count())
                .averagePurchasePrice(currentAssetPrice)
                .investmentStartDate(LocalDate.now())
                .wallet(wallet)
                .build();

        assetRepository.findAssetByWalletIdAndAssetName(wallet.getId(), incomingAsset.getName())
                .ifPresentOrElse(
                        existingAsset -> updateExistingAsset(existingAsset, incomingAsset),
                        () -> assetRepository.save(incomingAsset)
                );
        // TODO: Create asset history and add after this call
    }

    private BigDecimal calculateAssetValue(BigDecimal currentAssetPrice, BigDecimal count) {
        return currentAssetPrice.multiply(count);
    }

    private void updateExistingAsset(Asset existingAsset, Asset incomingAsset) {
        existingAsset.setValue(existingAsset.getValue().add(incomingAsset.getValue()));
        existingAsset.setCount(existingAsset.getCount().add(incomingAsset.getCount()));
        existingAsset.setAveragePurchasePrice(calculateAveragePurchasePrice(existingAsset, incomingAsset));

        assetRepository.save(existingAsset);
    }

    private BigDecimal calculateAveragePurchasePrice(Asset existingAsset, Asset incomingAsset) {
        BigDecimal combinedTotalValue = existingAsset.getValue().add(incomingAsset.getValue());
        BigDecimal combinedCount = existingAsset.getCount().add(incomingAsset.getCount());

        return combinedTotalValue.divide(combinedCount, RoundingMode.HALF_UP);
    }

    @Transactional
    public void deleteAsset(OidcUser oidcUser, Long assetId) {
        var wallet = customerService.findCustomerByEmail(oidcUser.getEmail()).getWallet();
        var asset = assetRepository.findById(assetId).orElseThrow(() ->
                new AssetNotFoundException("Asset with id %d not found".formatted(assetId)));

        if (wallet.getAssets().stream().anyMatch(a -> a.equals(asset))) {
                assetRepository.delete(asset);
        } else {
            throw new AssetOwnershipException("Asset with id %d not belong to your wallet.".formatted(assetId));
        }
    }

    @Transactional(readOnly = true)
    public List<AssetDTO> findAllWalletAssets(OidcUser oidcUser) {
        var customer = customerService.findCustomerByEmail(oidcUser.getEmail());
        var wallet = customer.getWallet();

        return assetRepository.findAllAssetsByWalletId(wallet.getId())
                .stream()
                .map(assetDTOMapper::mapToDTO)
                .collect(Collectors.toList());
    }
}