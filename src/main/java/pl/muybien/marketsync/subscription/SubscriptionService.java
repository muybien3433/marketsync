package pl.muybien.marketsync.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.muybien.marketsync.asset.AssetProviderFactory;
import pl.muybien.marketsync.asset.AssetService;
import pl.muybien.marketsync.asset.AssetServiceFactory;
import pl.muybien.marketsync.customer.Customer;
import pl.muybien.marketsync.customer.CustomerService;
import pl.muybien.marketsync.handler.InvalidSubscriptionParametersException;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final AssetServiceFactory assetServiceFactory;
    private final AssetProviderFactory assetProviderFactory;
    private final CustomerService customerService;

    @Transactional
    public void addSubscription(OidcUser oidcUser, String uri,
                                Double upperValueInPercent, Double lowerValueInPercent) {
        var service = assetServiceFactory.getService(uri);
        // TODO: After marketplace creation provide necessarily logic switching between providers
        var assetProvider = assetProviderFactory.getProvider("crypto");
        var currentAsset = assetProvider.fetchAsset(uri);
        String assetName = currentAsset.getName();
        BigDecimal currentAssetPrice = currentAsset.getPriceUsd();

        BigDecimal upperPriceInUsd = null;
        if (upperValueInPercent != null) {
            upperPriceInUsd = calculatePriceByClientPercentInput(currentAssetPrice, upperValueInPercent);
        }

        BigDecimal lowerPriceInUsd = null;
        if (lowerValueInPercent != null) {
            lowerPriceInUsd = calculatePriceByClientPercentInput(currentAssetPrice, lowerValueInPercent);
        }

        Customer customer = customerService.findCustomerByEmail(oidcUser.getEmail());

        if (upperValueInPercent != null || lowerValueInPercent != null) {
            service.createAndSaveSubscription(customer, assetName, upperPriceInUsd, lowerPriceInUsd);
        } else {
            throw new InvalidSubscriptionParametersException("At least one parameter must be provided.");
        }
    }

    private BigDecimal calculatePriceByClientPercentInput(BigDecimal assetPrice, Double valueInPercent) {
        BigDecimal percentDecimal = new BigDecimal(valueInPercent)
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        BigDecimal change = assetPrice.multiply(percentDecimal);

        return assetPrice.add(change).setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    public void removeSubscription(OidcUser oidcUser, String uri, Long id) {
        AssetService assetService = assetServiceFactory.getService(uri);
        assetService.removeSubscription(oidcUser, id);
    }
}
